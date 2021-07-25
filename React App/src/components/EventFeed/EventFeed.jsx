import {React, useState, useEffect} from 'react';
import axios from "axios";
import { getURL, getUsername, getToken } from "../../utils/index";
import Post from './EventPost';
import './EventFeed.css';
import Navbar from '../Navbar/Navbar';
import Loading from '../Loading/Loading';
import CreateEvent from '../CreateEvent/CreateEvent';
import RespNavbar from '../Navbar/RespNavbar'

export default function EventFeed() {

  const [allowedLocation, setAllowedLocation] = useState(false)

    useEffect(() => {
      navigator.permissions.query({
        name: 'geolocation'
        }).then((result) => {
          if(result.state !== "granted")
          {
            alert("Please Allow Location to use Milo.");
            window.location="/"
            return;
          }
          else
          {
            try{
              if(getUsername().length>0)
              {
                setAllowedLocation(true)
              }
              else
              {
                alert("Kindly login to keep using Milo.");
                window.location="/login"
                return;
              }
            }
            catch{
              alert("Kindly login to keep using Milo.");
                window.location="/login"
                return;
            }
          }
        });
    }, []
    )

    const token = getToken();
    const username = getUsername();
    const url = getURL();

    const [lat, setLat] = useState("");
    const [long, setLong] = useState("");
    const [locationState, setLocationState] = useState([]);
    const [screenReady, setScreenReady] = useState(false);
    const [event, setEvent] = useState([])

    function error(err) {
        alert("Could not get your Co-ordinates.");
        window.location = "/"
    }
    

    useEffect (
        () => {
            navigator.geolocation.getCurrentPosition((position) => {
                setLat(position.coords.latitude)
                setLong(position.coords.longitude)
                axios({
                    method: "GET",
                    url:
                      url + "/api/eventfeed/"+username+"/"+position.coords.latitude+"/"+position.coords.longitude,
                    headers: {
                      "Content-Type": "application/json",
                      Authorization: `${token}`,
                    },
                  })
                    .then((response) => {
                      const data = response.data;
                      if(data.status === 401)
                      {
                        alert("Your Session has expired or you are not in the position to access the post.")
                        window.location = "/login"
                      }
                      if(data.status === 500)
                      {
                        alert("Some error occurred");
                        window.location = "/"
                      }
                      setEvent(data);
                      setScreenReady(true)
                    })
                    .catch((err) => {
                      alert("Something went wrong!");
                    });
              }, error, {enableHighAccuracy: true, maximumAge: 0})
        }, []
    )
    
      useEffect( () => {
        navigator.permissions.query({
          name: 'geolocation'
        }).then((result) => {
            setLocationState(result.state)
        });}, [locationState])

        let thePosts = []
        for(let i=0; i<event.length; i++)
        {
            thePosts.push(<Post key={i+1} data={event[i]} />)
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

        
        return (
          <>
          {screenReady&&allowedLocation ?
            <div className="fluid-container g-0 mw-100 feed-page">
              <div className="row g-0 blur-container">
              {showBigNavbar || window.innerWidth>1000 ?null:<RespNavbar/>}
                <div className="col-lg-2 col-md-12 col-sm-12 post-navbar">
                {showBigNavbar || window.innerWidth>1000 ? <Navbar/> : null}
                </div>
                <div className="col-lg-6 col-md-10 col-sm-10 post-feed">
                <div className="container container-fluid">
                {thePosts.map((curele) => curele)}
                </div> 
              </div>
              <div className="col-lg-3 col-md-10 col-sm-10 create-event">
              <CreateEvent/>
              </div> 
                </div>        
        </div>
        : <Loading/>}
        </>
        )
}