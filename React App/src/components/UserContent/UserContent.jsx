import {React, useState, useEffect} from 'react';
import UserPosts from './UserPosts/UserPosts'
import UserEvents from './UserEvents/UserEvents'
import { getURL, getUsername, getToken } from "../../utils/index";
import UserPromotions from './UserPromotions/UserPromotions'
import './UserContent.css';
import Navbar from '../Navbar/Navbar';
import RespNavbar from '../Navbar/RespNavbar'
import Loading from '../Loading/Loading';

export default function UserContent() {

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
    return (<>
        { allowedLocation ?
        <div className="fluid-container g-0 mw-100 feed-page">
          <div className="row g-0 blur-container">
          {showBigNavbar || window.innerWidth>1000 ?null:<RespNavbar/>}
            <div className="col-lg-2 col-md-12 col-sm-12 post-navbar">
              {showBigNavbar || window.innerWidth>1000 ? <Navbar/> : null}
              
            </div>
            <div className="col-lg-6 col-md-10 col-sm-10 post-feed">
            <div className="container container-fluid">
              <p className="light-bg" style={{textAlign: "center", fontSize: "2rem", color: "#B982FC",fontWeight: "600", margin: "4% auto"}}>Posts</p>
            <UserPosts/>
            <p className="light-bg" style={{textAlign: "center", fontSize: "2rem", color: "#B982FC",fontWeight: "600", margin: "4% auto"}}>Events</p>
            <UserEvents/>
            <p className="light-bg" style={{textAlign: "center", fontSize: "2rem", color: "#B982FC",fontWeight: "600", margin: "4% auto"}}>Promotions</p>
            <UserPromotions/>
            </div> 
          </div>
          <div className="col-lg-3 col-md-11 col-sm-11 create-post">
            <div className="container container-fluid" style={{top: "3%", position: "sticky", margin: "5% auto"}}>
              <div className="container container-fluid light-bg" style={{margin: "5% auto"}}><img className="img-fluid" src="/images/1.svg" alt="" /></div>
            </div>
          </div> 
            </div>        
    </div> : <Loading/>}</>
    )

}
