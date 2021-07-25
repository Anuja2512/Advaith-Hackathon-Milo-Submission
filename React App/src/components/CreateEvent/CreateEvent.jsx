import {React, useState, useEffect} from 'react';
import axios from "axios";
import { getURL, getUsername, getToken } from "../../utils/index";
import { Button } from 'react-bootstrap';
import './CreateEvent.css';
import fileStyle from '../CreatePost/FileStyle.module.css';

export default function CreateEvent() {

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
    
    const [formSubmitted, setFormSubmitted] = useState(false)
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
        const [eventDate, setEventDate] = useState()

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
        const eventDateHandler = (e) => {
            let thatDate = new Date(e.target.value)
            thatDate = ''+Math.floor(Number(thatDate)/1000.0)
            setEventDate(thatDate)
        }

        const createEventHandler =(e) => {
            e.preventDefault()
            setFormSubmitted(true)
            if(imageLink.type!=="image/png" && imageLink.type!=="image/jpg" && imageLink.type!=="image/jpeg")
            {
                alert("Invalid File-Type. Try Again.");
                window.location = "/createevent";
                return 0;
            }
            let theURL = url + '/api/events'
            let body = {
                "title": title,
                "imageLink": "",
                "description": desc,
                "username": username,
                "deadlineTimeEpoch": eventDate,
                "venueLatitude": ''+lat,
                "venueLongitude": ''+long,
                "radius": radius
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
                                    window.location = '/eventfeed'
                                }
                                alert("Event created Successfully")
                                window.location = "/eventfeed"
                                }
                        }
                        )
                        .catch(
                            err => {
                                alert("Some error occurred")
                                window.location = '/eventfeed'
                                return 0;
                                }
                        );
                    })
                    .catch((error) => {
                alert("Problem with Image Upload")
                window.location = '/eventfeed'
                return 0;
                    });
        }

    return (
        <div style={{position:'sticky', top:'4%'}} className="container-fluid crtbg">
                <div style={{marginLeft:'0%'}} className="row cont">
                <div className="col">
                <br />
                <h3 style={{color:'#696969', fontWeight: "500"}}>Add an Event</h3>
                <br />
            <form onSubmit={createEventHandler}>
            <div class="form-group">
                <label htmlFor="title" class="form-label">Title</label>
                <input onChange={titleHandler} class="form-control post-textarea" type="text" style={{padding:'3% 6% 4% 6%'}} placeholder="The Best Thing in Town..." required/>
                <br />
                <label htmlFor="desc" class="form-label">Description</label>
                <textarea onChange={descHandler} class="form-control"
                style={{padding:'4% 6%'}}
                className="form-control post-textarea" 
                id="exampleFormControlTextarea1" rows="3" placeholder="The what, the how, the why..." required></textarea>
                <br />
                <label for="customRange2" class="form-label">Radius: {radius} km</label>
                <div className="post-slider">
                <input type="range" style={{padding:'6% 4% 4% 4%'}} onChange={radiusHandler} className="form-range" min="1" max="150" id="customRange2" defaultValue="1" required></input></div>
                <br /><label for="start">Event Date:</label>
                <div style={{width: 'fit-content', borderRadius: '1.8rem',color: '#696969', background:'linear-gradient(to bottom right, rgba(255,255,255,0.7),rgba(255,255,255,0.3))',padding:'0 0 0 2%',marginTop:'2%'}} classname="date-sel">
                
                <input type="date" id="start" name="event" onChange={eventDateHandler} required></input></div>
               
                <input className={fileStyle.customFileInput} onChange={fileHandler} type="file" name="Image" />
                <Button 
              className="btn btn-primary post-btn"
              style={{
                backgroundColor:"#B76FFF",
                borderRadius:'2rem',
                width:'100%',
                marginTop: '0%',
                outline:'none',
                border:'none'
              }}
              disabled={formSubmitted}
as="input" type="submit" value="Create Event" />{' '}
            </div>
            </form>
            </div>
            </div>
        </div>
    )
}
