import {React, useState, useEffect} from 'react';
import axios from "axios";
import { getURL, getUsername, getToken } from "../../utils/index";
import Comments from './Comments';
import AddComment from './AddComment';
import Promotion from './Promotion';
import Loading from '../Loading/Loading';
import '../Feed/Feed.css';
import '../ParticularPost/ParticularPost.css';
import RespNavbar from '../Navbar/RespNavbar'
import Navbar from '../Navbar/Navbar';

export default function ParticularPromotion({
    match: {
      params: { id },
    },
  }) {

    const token = getToken();
    const username = getUsername();
    const url = getURL();

    const [lat, setLat] = useState("");
    const [long, setLong] = useState("");
    const [locationState, setLocationState] = useState([]);
    const [screenReady, setScreenReady] = useState(false);
    const [event, setEvent] = useState()

    function error(err) {
        alert("Could not get your Co-ordinates.");
        window.location = "/"
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


    useEffect (
        () => {
            navigator.geolocation.getCurrentPosition((position) => {
                setLat(position.coords.latitude)
                setLong(position.coords.longitude)
                axios({
                    method: "GET",
                    url:
                      url + "/api/promotion/"+id+"/"+position.coords.latitude+"/"+position.coords.longitude,
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
                        window.location = "/promotionfeed"
                      }
                      setEvent(data);
                      console.log(data)
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
    
        return (
        <>
      {screenReady&&allowedLocation ?
        <div className="fluid-container g-0 mw-100 feed-page">
          <div className="row g-0 blur-container">
          {showBigNavbar || window.innerWidth>1000 ?null:<RespNavbar />}
            <div className="col-lg-2 col-md-12 col-sm-12 post-navbar">
              {showBigNavbar || window.innerWidth>1000 ? <Navbar/> : null}
              
            </div>
            <div className="col-lg-6 col-md-10 col-sm-10 post-feed">
            <div className="container container-fluid">
            {/* <div className="col post-cont"> */}
              <div className="light-bg">
              { screenReady ? <Promotion id={id} data={event} /> : null}
              { screenReady ? <AddComment id={id} data={event} /> : null}
              { screenReady ? <Comments id={id} data={event} /> : null }
              </div>                    
            {/* </div>     */}
            </div> 
          </div>
          <div className="col-lg-3 col-md-11 col-sm-11 create-post">
          <div className="container container-fluid" style={{top: "3%", position: "sticky", margin: "5% auto"}}>
              <div className="container container-fluid light-bg" style={{margin: "5% auto"}}><img className="img-fluid" src="/images/2.svg" alt="" /></div>
            </div>
          </div> 
            </div>        
    </div>
    : <Loading/>}
    </>
        )
}

