import {React, useState, useEffect} from 'react';
import axios from "axios";
import { getURL, getUsername, getToken } from "../../utils/index";
import './CreatePost.css';
import { Button } from 'react-bootstrap';
import fileStyle from './FileStyle.module.css'

export default function CreatePost() {

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

    const createPostHandler = (e) => {
        e.preventDefault();
        setFormSubmitted(true)
        const theURL = url + '/api/posts'
        let theArray = hashText.split(" ");
          let theArray2 = []
          for(let i=0; i<theArray.length; i++)
          {
            if(theArray[i].length > 0)
            {
                let someString = ""
                if(theArray[i][0] !== "#")
                {
                    someString += "#"+theArray[i]
                }
                else
                {
                    someString += theArray[i]
                }
                someString = someString.toLowerCase()
                theArray2.push(someString)
            }
          }
          let hashSets = new Set(theArray2)
          let hashArray = [...hashSets]
          let body = {
            "username": username,
            "data": data,
            "imageLink": "",
            "hashtags": hashArray,
            "latitude": lat,
            "longitude": long,
            "radius": radius,
            "timeEpoch": timeEpoch
          }
        //without image
        console.log(imageLink)
        if(imageLink.type === "empty")
        {
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
                          window.location = '/feed'
                      }
                      alert("Post created Successfully")
                      window.location = "/feed"
                      }
                }
              )
              .catch(
                  err => {
                      alert("Some error occurred")
                      window.location = '/feed'
                      return 0;
                      }
              ); 
        }
        else
    {
        //with image
        if(imageLink.type!=="image/png" && imageLink.type!=="image/jpg" && imageLink.type!=="image/jpeg")
        {
          alert("Invalid File-Type. Try Again.");
          window.location = "/feed";
          return 0;
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
                    console.log(body)
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
                                window.location = '/feed'
                            }
                            alert("Post created Successfully")
                            window.location = "/feed"
                            }
                    }
                    )
                    .catch(
                        err => {
                            alert("Some error occurred")
                            window.location = '/feed'
                            return 0;
                            }
                    );
                })
                .catch((error) => {
            alert("Problem with Image Upload")
            window.location = '/feed'
            return 0;
                });
    }
    }

    const [data, setData] = useState("")
    const [imageLink, setImageLink] = useState({type: 'empty'});
    const [radius, setRadius] = useState("1")
    const [timeEpoch, setTimeEpoch] = useState("")
    
    useEffect(()=>{
        setTimeEpoch(''+Math.floor(Number(Date.now())/1000.0))
    }, [])

    const [hashText, setHashText] = useState("")

    const postHandler = (e) => {
        setData(e.target.value)
    }
    const radiusHandler = (e) => {
        setRadius(e.target.value)
    }
    const hashHandler = (e)=> {
        setHashText(e.target.value)
    }
    const fileHandler = (e) => {
        setImageLink(e.target.files[0]);
    }
      
    const [formSubmitted, setFormSubmitted] = useState(false)


    return (
        <div style={{position:'sticky', top:'4%'}} className="container-fluid">
            <div className="row cont">
                <div className="col">
                <h3 style={{color:'#696969'}}>Let's Write A Post Here</h3>
                <form onSubmit={createPostHandler}>
                <div className="form-group">
                <textarea style={{padding:'2% 5%'}} onChange={postHandler} className="form-control post-textarea"
                 id="exampleFormControlTextarea1" rows="3" placeholder="What's on your mind..." required></textarea>
                <br />
                <label htmlFor="hashtags" className="form-label">Hashtags</label>
                <input onChange={hashHandler} className="form-control post-hashtags" type="text" placeholder="Hashtags" required/><br />
                <label htmlFor="customRange2" className="form-label">Radius: {radius} km</label>
                <div className="post-slider">
                <input type="range" style={{padding:'6% 4% 4% 4%'}} onChange={radiusHandler} className="form-range" min="1" max="150" id="customRange2" defaultValue="1" required></input>
                </div>
                <input onChange={fileHandler} type="file" name="Image" className={fileStyle.customFileInput} />
                <Button 
              className="btn btn-primary post-btn"
              style={{
                backgroundColor:"#B76FFF",
                borderRadius:'2rem',
                width:'100%',
                marginTop: '2%',
                outline:'none',
                border:'none',
                marginLeft: "auto",
                marginRight: "auto"
              }}
              disabled={formSubmitted}
              as="input" type="submit" value="Post" />{' '}
            </div>
            </form>
            </div>
            </div>
        </div>
    )
}
