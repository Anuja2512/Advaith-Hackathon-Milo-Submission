import {React, useState, useEffect} from 'react'
import axios from "axios";
import { getURL, getUsername, getToken } from "../../utils/index";
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import './Event.css';
import '../ParticularPost/ParticularPost.css';
export default function Event(props) {

    const handleClose = () => {
        setOpen(false);
        window.location="/event/"+data.key
      };

      const [open, setOpen] = useState(true);
      const [showDeletePrompt, setShowDeletePrompt] = useState(false)


    //   const handleClickOpen = () => {
    //     setOpen(true);
    //   };

    function EpochToDate(epoch) {
        if (epoch < 10000000000)
            epoch *= 1000; // convert to milliseconds (Epoch is usually expressed in seconds, but Javascript uses Milliseconds)
        var epoch = epoch + (new Date().getTimezoneOffset() * -1); //for timeZone        
        return new Date(epoch);
    }

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

    function Epoch(date) {
        return Math.round(new Date(date).getTime() / 1000.0);
    }

    const deleteMyPostHandler = () => {
        axios.delete(url + "/api/event/"+data.key, {
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
            window.location = "/event/"+data.key
            return
          }
          alert("Event Deleted Successfully.")
          window.location = "/eventfeed"
          return
        }
      })
      .catch(
        () => {
        alert('Some Error Occurred. We got this.')
        window.location = "/event/"+data.key
        });
    }

    const [showUpdateForm, setShowUpdateForm] = useState(false);

    const deleteDialog = <Dialog open={open} onClose={handleClose} aria-labelledby="alert-dialog-title" aria-describedby="alert-dialog-description">
            <DialogTitle id="alert-dialog-title">{"Delete Event"}</DialogTitle>
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

    const id = props.id
    const data = props.data
    const [date, setDate] = useState()
    const [readyScreen, setReadyScreen] = useState(false)

    useEffect(()=> {
        setDate(EpochToDate(props.data.deadlineTimeEpoch))
        setReadyScreen(true)
    }, [])

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

        axios.put(url + "/api/updateevent/"+data.key, body, {
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
            alert("Event does not exist.")
            window.location="/event/"+data.key
          }
          alert("Event Updated Successfully.")
          window.location="/event/"+data.key
        }
      })
      .catch((error) => {
        alert("Some Error Occurred")
        window.location="/event/"+data.key
        return 0;
            });
    }

    const [showBigNavbar, setShowBigNavbar] = useState(true)
    // const [innerWidthScreen, setInnerWidthScreen] = useState(window.innerWidth)

    // setInnerWidthScreen(window.innerWidth)

    useEffect(()=>{
      if(width > 1000)
      {
        setShowBigNavbar(true);
      }
      else
      {
        setShowBigNavbar(false);
      }
    }, [])

    const [width, setWidth] = useState(window.innerWidth);
    const [height, setHeight] = useState(window.innerHeight);
    const updateWidthAndHeight = () => {
      setWidth(window.innerWidth);
      setHeight(window.innerHeight);
      if(window.innerWidth > 1000)
      {
        setShowBigNavbar(true)
      }
      else
      {
        setShowBigNavbar(false)
      }
    };
    useEffect(() => {
      window.addEventListener("resize", updateWidthAndHeight);
      return () => window.removeEventListener("resize", updateWidthAndHeight);
    }, []);

    const updateForm = <form onSubmit={updateFormHandler}>
                <label htmlFor="customRange2" className="form-label">Radius: {radius}</label>
                <input type="range" onChange={radiusHandler} className="form-range" min="1" max="150" id="customRange2" defaultValue={data.radius} required></input>
                <br />
                <button style={{backgroundColor: 'transparent'}} type="submit"><img style={{width:'3.5em'}} classname="nav-img" src="/images/globe.svg" alt="radius"/></button><br /><br />
        </form>

    return(
        <>{readyScreen ? <>
        <div className="post-contain">
            <div className="container-fluid" onClick={userClickHandler}>
                <div className="row pap">
                <div className="col-lg-3 col-md-3 col-sm-3 pap-prof-pic-container dp-cont">
                { data.user.profilePicture.length > 0 ? <img src={data.user.profilePicture} className="rounded post-profile-pic float-right" alt="profilepicture" /> : <img src="/user.png" className="img img-fluid rounded post-profile-pic" alt="profilepicture" /> }
                </div>
                <div className="col-lg-9 col-md-9 col-sm-9 col-9 text-left">
                        <p className="name">{data.user.fName} {data.user.lName}</p>
                        <p className="username">@{data.user.username}</p>
                        <p className="headline">{data.user.headline}</p>
                </div>
                </div>
            </div>
        <div className="container-fluid event-cont">
            <h3 className="event-title">{data.title}</h3>
            <h6 className="event-desc">{data.description}</h6>
            <h6 className="event-date">{date.getDate()} - {date.getMonth()} - {date.getFullYear()}</h6>
            {showBigNavbar || window.innerWidth>1000 ? <>{ data.imageLink.length > 0 ? <div className="post-pic-container"><img src={data.imageLink} className="img img-fluid post-pic" alt="postpicture"/></div> : null}</> : <>{ data.imageLink.length > 0 ? <div className="post-pic-container text-center"><img src={data.imageLink} className="img-fluid post-pic" style={{width: "100%"}} alt="postpicture"/></div> : null}</>}
        </div>
        {data.user.username===username ? updateAndDelete : null}
            {showUpdateForm ? updateForm : null}
            {showDeletePrompt ? deleteDialog : null}
    </div></> : null}</>)
}

