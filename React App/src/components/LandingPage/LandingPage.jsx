import {React, useState, useEffect} from 'react'
import './LandingPage.css';

export default function LandingPage() {

    navigator.geolocation.getCurrentPosition((position) => {
        setLat(position.coords.latitude)
        setLong(position.coords.longitude)
      }, error, {enableHighAccuracy: true, maximumAge: 0})
    
        function error(err) {
          console.warn(`ERROR(${err.code}): ${err.message}`);
        }
    
      const [lat, setLat] = useState("");
      const [long, setLong] = useState("");



    return (
        <div className="fluid-container g-0 mw-100 feed-page">
            <div className="blur-container">
                <div className="container container-fluid">
                    <div className="row">
                        <div className="col-lg-6 col-md-12 col-sm-12 landing-cont">
                        <div className="row ">
                                    <div className="col-lg-12 col-md-12 col-sm-12 text-center">
                                        <div className="logo landing-head" style={{fontSize: "4rem"}}>Milo</div>
                                    </div>
                                </div>
                            <div className="container-fluid landing-bottom" style={{margin: "0 auto 10% auto"}}>
                                
                                <div className="row">
                                <div className="col-lg-12 col-md-12 col-sm-12 text-center">
                                        <div style={{fontFamily: "Zen Loop", fontSize: "3.9rem", color: "#B76FFF", fontWeight: "600"}}>Welcome to Milo</div>
                                        
                                </div>
                                <br /><br /><br /><br />
                                <div className="col-lg-12 col-md-12 col-sm-12 text-left">
                                        <div className="landingPageText container-fluid" style={{margin: '3%', color: '#2e2e2e'}}>
                                            Milo is the next-generation Social Network. One where you control how far should your content be propagated. What's it going to be? Some gossip of the next-door, some fantastic event you've been planning for a while, or promoting or side hustle? Well, your friendly neighbourhood Milo has you covered!<br/><br/>
                                            Choose from posting Milos - small messages you propagate for 24 hours, or adding Events that show up to the people under the radius till the scheduled date or Burps - spreading the word of mouth of ideas, professions or literally anything! Milo has something for everyone!! 
                                        </div>
                                </div>
                                <br /><br /><br /><br />
                                <div className="container container-fluid text-center" style={{margin: "3% auto"}}>
                                    <div className="row">
                                    <div className="col-lg-6 col-md-6 col-sm-6">
                                    <a href="/signup" className="btn btn-lg landingPageButton" style={{color: "#ffffff"}}>Get Started</a>
                                    </div>
                                    <div className="col-lg-6 col-md-6 col-sm-6">
                                    <a href="/login" className="btn btn-lg landingPageButton" style={{color: "#ffffff"}}>Login</a>
                                    </div>
                                    </div>
                                    </div>
                                </div>
                                <div className="container container-fluid" style={{margin: "4% auto"}}>
                                    <img className=" img-fluid" src="./images/landingPageRight.png" alt="theWhat" />
                                </div><br/>
                            </div>
                            <div className="app-container">
                                <p className="app">You Can Also Try Out Our Android App Now</p>
                            <a href="https://drive.google.com/drive/folders/1KAgGQCYlfi-IXglEEf47tV89v0dCzIrL?usp=sharing" className="btn btn-lg landingPageButton" style={{color: "#ffffff", marginLeft:'5%'}}>Download Now</a>
                            </div>
                            </div>
                            <div className="col-lg-6 col-md-12 col-sm-12">
                            <div className="container container-fluid" style={{margin: "20% auto"}}>
                            <img className="img-log img-fluid" src="./images/landingPageLeft.svg" alt="theWhat" />
                                </div>
                            </div>
                        </div>
                </div>
            </div>
        </div>
    )
}