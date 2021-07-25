import {React, useState} from 'react'
import axios from "axios";
import { getURL, getUsername, getToken } from "../../utils/index";
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import '../ParticularPost/ParticularPost.css';
import './Promotion.css';

export default function Promotion(props) {

    const handleClose = () => {
        setOpen(false);
        window.location="/promotion/"+data.key
      };

      const [open, setOpen] = useState(true);
      const [showDeletePrompt, setShowDeletePrompt] = useState(false)

    
    const data = props.data

    const username = getUsername()
    const url = getURL()
    const token = getToken();

    const userClickHandler = (e) => {
        window.location="/profile/"+data.user.username
    }

    const updateHandler = (e) => {
        if(showUpdateForm === true)
        {
            setShowUpdateForm(false)
        }
        else
        {
            setShowUpdateForm(true)
        }
    }

    const deleteMyPostHandler = () => {
        axios.delete(url + "/api/promotion/"+data.key, {
        headers: {
          "Content-Type": "application/json",
          Authorization: token,
        },
      })
      .then((response) => {
        console.log(response);
        if (response.status === 200) {
          if(response.data.status === 401)
          {
            alert("Your session has expired. Kindly Login.")
            window.location="/login"
          }
          else if(response.data.status === 404)
          {
            alert(response.data.message)
            window.location = "/promotion/"+data.key
            return
          }
          alert("Promotion Deleted Successfully.")
          window.location = "/promotionfeed"
          return
        }
      })
      .catch(
        () => {
        alert('Some Error Occurred. We got this.')
        window.location = "/promotion/"+data.key
        });
    }

    const [showUpdateForm, setShowUpdateForm] = useState(false);

    const deleteDialog = <Dialog open={open} onClose={handleClose} aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
            <DialogTitle id="alert-dialog-title">{"Delete Promotion"}</DialogTitle>
            <DialogContent>
                <DialogContentText id="alert-dialog-description">
                    Are you sure you want to delete? There's no going back..
                </DialogContentText>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose} color="primary">
                    No
                </Button>
                <Button onClick={deleteMyPostHandler} color="primary" autoFocus>
                    Yes
                </Button>
            </DialogActions>
        </Dialog>

const deleteHandler = (e) => {
    console.log("Delete")
    setShowDeletePrompt(true)
    
}
let updateAndDelete = <>
<br />
<button style={{backgroundColor: 'transparent'}} onClick={updateHandler}><img style={{width:'3.5em', backgroundColor: 'none'}} classname="nav-img" src="/images/update.svg" alt="update"/></button>&nbsp;&nbsp;&nbsp;
    <button style={{backgroundColor: 'transparent'}} onClick={deleteHandler}><img style={{width:'3.5em'}} classname="nav-img" src="/images/delete.svg" alt="delete"/></button>
    
    <br /><br />
</>

const radiusHandler = (e) => {
    setRadius(e.target.value)
}
const [radius, setRadius] = useState(data.radius)

const updateFormHandler = (e) => {
    e.preventDefault()
    const body = {
        "radius": radius
    }

    axios.put(url + "/api/updatepromotion/"+data.key, body, {
        headers: {
          "Content-Type": "application/json",
          Authorization: token,
        },
      })
      .then((response) => {
        if (response.status === 200) {
          const data = response.data
          if(data.status === 401)
          {
            alert("Your session has expired. Kindly Login.")
            window.location="/login"
          }
          else if(data.status === 404)
          {
            alert("Promotion does not exist.")
            window.location="/promotion/"+data.key
          }
          alert("Promotion Updated Successfully.")
          window.location="/promotion/"+data.key
        }
      })
      .catch((error) => {
        alert("Some Error Occurred")
        window.location="/promotion/"+data.key
        return 0;
            });
    }

    const updateForm = <form onSubmit={updateFormHandler}>
                <label for="customRange2" className="form-label">Radius: {radius}</label>
                <input type="range" onChange={radiusHandler} className="form-range" min="1" max="150" id="customRange2" defaultValue={data.radius} required></input>
                <button style={{backgroundColor: 'transparent'}} type="submit"><img style={{width:'3.5em'}} classname="nav-img" src="/images/globe.svg" alt="radius"/></button><br /><br />
        </form>

    return (
      <div className="post-contain">
      <div className="container-fluid" onClick={userClickHandler}>
          <div className="row pap">
          <div className="col-lg-3 col-md-3 col-sm-3 prof-pic-container dp-cont">
          { data.user.profilePicture.length > 0 ? <img src={data.user.profilePicture} className="rounded post-profile-pic float-right" alt="profilepicture" /> : <img src="/user.png" className="img img-fluid rounded post-profile-pic" alt="profilepicture" /> }
          </div>
          <div className="col-lg-9 col-md-9 col-sm-9 col-9 text-left">
              <p className="name">{data.user.fName} {data.user.lName}</p>
              <p className="username">@{data.user.username}</p>
              <p className="headline">{data.user.headline}</p>
          </div>
          </div>
      </div>  
        <div className="container-fluid promotion-cont">
            <p className="promotion-title">{data.title}</p>
            <p className="promotion-profession">{data.profession}</p>
            <p className="promotion-desc">{data.description}</p>
            { data.imageLink.length > 0 ? <div className="post-pic-container"><img src={data.imageLink} style={{width:'100%'}} className=" post-pic"  alt="eventImage"/></div> : null}         
        </div>
        {data.user.username===username ? updateAndDelete : null}
            {showUpdateForm ? updateForm : null}
            {showDeletePrompt ? deleteDialog : null}
       
        
    </div>
    )
}

