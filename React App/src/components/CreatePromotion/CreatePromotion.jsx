import {React, useState, useEffect} from 'react';
import axios from "axios";
import { getURL, getUsername, getToken } from "../../utils/index";
import { Button } from 'react-bootstrap';
import fileStyle from '../CreatePost/FileStyle.module.css';
import './CreatePromotion.css';

export default function CreatePromotion() {


    const token = getToken();
    const username = getUsername();
    const url = getURL();

    const [lat, setLat] = useState("");
    const [long, setLong] = useState("");
    const [locationState, setLocationState] = useState([]);

    function error(err) {
        alert("Could not get your Co-ordinates.");
        window.location = "/"
    }
    

    navigator.geolocation.getCurrentPosition((position) => {
        setLat(position.coords.latitude)
        setLong(position.coords.longitude)
      }, error, {enableHighAccuracy: true, maximumAge: 0})

      useEffect( () => {
        navigator.permissions.query({
          name: 'geolocation'
        }).then((result) => {
            setLocationState(result.state)
        });}, [locationState])

        const [imageLink, setImageLink] = useState({type: 'empty'});
        const [radius, setRadius] = useState("1")
        const [desc, setDesc] = useState("")
        const [title, setTitle] = useState("")
        const [profession, setProfession] = useState("")
        const [formSubmitted, setFormSubmitted] = useState(false)
    
        const radiusHandler = (e) => {
            setRadius(e.target.value)
        }
        const fileHandler = (e) => {
            setImageLink(e.target.files[0]);
        }
        const descHandler = (e) => {
            setDesc(e.target.value)
        }
        const titleHandler = (e) => {
            setTitle(e.target.value)
        }
        const professionHandler = (e) => {
            setProfession(e.target.value)
        }

        const createPromotionHandler = (e) => {
            e.preventDefault();
            setFormSubmitted(true)
            if(imageLink.type!=="image/png" && imageLink.type!=="image/jpg" && imageLink.type!=="image/jpeg")
            {
                alert("Invalid File-Type. Try Again.");
                window.location = "/createevent";
                return 0;
            }
            let theURL = url + '/api/promotions'
            let body = {
                "title": title,
                "profession": profession,
                "imageLink": "",
                "description": desc,
                "latitude": lat,
                "longitude": long,
                "radius": radius,
                "username": username
              }
            //upload that image and get a link
            const formData = new FormData();
            formData.append('file', imageLink);
            fetch(
                    getURL()+'/api/uploadimage',
                    {
                        method: 'POST',
                        body: formData,
                    }
                )
                .then((response) => response.json())
                    .then((result) => {
                        body['imageLink'] = result.link
                        axios.post(
                        theURL,
                        body,
                        {
                            headers: {
                                'Content-Type': 'application/json',
                                'Authorization': `${token}`
                            }
                        }
                        ).then(
                        response => {
                            if(response.status === 200){
                                const data = response.data;
                                if(data.status === 401)
                                {
                                    alert("Your Session has expired.")
                                    window.location = "/login"
                                    return 0;
                                }
                                if(data.status === 500)
                                {
                                    alert(data.message);
                                    window.location = '/promotionfeed'
                                }
                                alert("Promotion created Successfully")
                                window.location = "/promotionfeed"
                                }
                        }
                        )
                        .catch(
                            err => {
                                alert("Some error occurred")
                                window.location = '/promotionfeed'
                                return 0;
                                }
                        );
                    })
                    .catch((error) => {
                alert("Problem with Image Upload")
                window.location = '/promotionfeed'
                return 0;
                    });
        }



    return (
        <div style={{position:'sticky', top:'4%'}} className="container-fluid">
            <div className="row cont pr-scr">
                <div className="col">
                <br />
                <h3 style={{color:'#696969', fontWeight: "500"}}>Add a Blurb</h3>
                <br />            
                <form onSubmit={createPromotionHandler}>
            <div className="form-group">
                <label htmlFor="title" className="form-label">Title</label>
                <input style={{padding:'3% 6% 4% 6%'}} onChange={titleHandler} className="form-control" type="text" placeholder="Work.." className="form-control post-textarea" required/>
                <br />
                <label htmlFor="profession" className="form-label">Profession</label>
                <input style={{padding:'3% 6% 4% 6%'}} onChange={professionHandler} className="form-control" type="text" placeholder="Profession I do.." className="form-control post-textarea" required/>
                <label style={{marginTop:'6%'}} htmlFor="desc" className="form-label">Description</label>
                <textarea style={{padding:'3% 6% 2% 6%'}} onChange={descHandler} className="form-control" id="exampleFormControlTextarea1" rows="3" placeholder="The what, the how, the why..." className="form-control post-textarea" required></textarea>
                <label style={{marginTop:'4%'}} htmlFor="customRange2" className="form-label">Radius: {radius} km</label>
                <div className="post-slider">
                <input type="range" style={{padding:'6% 4% 4% 4%'}} onChange={radiusHandler} class="form-range" min="1" max="150" id="customRange2" defaultValue="1" required></input></div>
                <br />
                <input  className={fileStyle.customFileInput}  onChange={fileHandler} type="file" name="Image" />
                <br />
                <Button 
              className="btn btn-primary post-btn"
              style={{
                backgroundColor:"#B76FFF",
                borderRadius:'2rem',
                width: '100%',
                marginTop: '0%',
                padding:'3% 10%',
                outline:'none',
                border:'none'
              }}
              disabled={formSubmitted}
              as="input" type="submit" value="Add Blurb" />{' '}
            </div>
            </form>
            </div>
            </div>            
        </div>
    )
}
