import { React, useState, useEffect } from "react";
import axios from "axios";
import { getToken, getURL, getUsername } from "../../utils/index";
import Loading from "./../Loading/Loading";
import "./UserProfile.css";
import Navbar from "../Navbar/Navbar";
import RespNavbar from "../Navbar/RespNavbar";

export default function UserProfile({
  match: {
    params: { username },
  },
}) {
  const token = getToken();
  const url = getURL();

  const [profile, setProfile] = useState();
  const [screenReady, setScreenReady] = useState(false);

  const [showBigNavbar, setShowBigNavbar] = useState(true);
  // const [innerWidthScreen, setInnerWidthScreen] = useState(window.innerWidth)

  // setInnerWidthScreen(window.innerWidth)

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

  useEffect(() => {
    if (width > 1000) {
      setShowBigNavbar(true);
    } else {
      setShowBigNavbar(false);
    }
  }, []);

  const [width, setWidth] = useState(window.innerWidth);
  const [height, setHeight] = useState(window.innerHeight);
  const updateWidthAndHeight = () => {
    setWidth(window.innerWidth);
    setHeight(window.innerHeight);
    if (window.innerWidth > 1000) {
      setShowBigNavbar(true);
    } else {
      setShowBigNavbar(false);
    }
  };
  useEffect(() => {
    window.addEventListener("resize", updateWidthAndHeight);
    return () => window.removeEventListener("resize", updateWidthAndHeight);
  }, []);

  useEffect(() => {
    axios({
      method: "GET",
      url: url + "/api/profile/" + username,
      headers: {
        "Content-Type": "application/json",
        Authorization: `${token}`,
      },
    })
      .then((response) => {
        const data = response.data;
        if (data.status === 401) {
          alert("Your Session has expired.");
          window.location = "/login";
        }
        if (data.status === 404) {
          alert("Profile does not exist");
          window.location = "/feed";
        }
        setProfile(data);
        setScreenReady(true);
      })
      .catch((err) => {
        alert("Somthing went wrong!");
      });
  });

  return (
    <>
      {screenReady && allowedLocation ? (
        <div className="fluid-container g-0 mw-100 bg">
          <div className="row g-0 blur-container">
            {showBigNavbar || window.innerWidth > 1000 ? null : <RespNavbar />}
            <div className="col-lg-2 col-md-12 col-sm-12 post-navbar">
              {showBigNavbar || window.innerWidth > 1000 ? <Navbar /> : null}
            </div>
            <div className="col-lg-9 col-md-12 col-sm-12 profile-container">
              <div
                style={{
                  paddingLeft: "6%",
                  position: "relative",
                  height: "97%",
                }}
                className="light-bg light-cont"
              >
                <div className=" container-fluid">
                  <div className="col-lg-12 col-md-12 col-sm-12">
                    <div style={{width:'fit-content'}} className="pic-cont update-pic-cont">
                      { profile.profilePicture.length > 0 ? <img 
                      style={{borderRadius: '50%',
                      height:'170px',
                      width:'170px',  transform: 'translateY(-40%)',
                      display:'inline-block'
                    }}
                    src={profile.profilePicture} className=" img-fluid" alt="profilepicture"/> : <img src="/user.png" className="img-fluid" alt="profilepicture" /> }
                  </div>
                  <div className="col-6" style={{display:'inline-block',width:'60%'}}>
                    <div style={{ display: "inline-block", margin: "0% 4%" }}>
                    <p className="profile-username profile-title">
                      @{profile.username}
                    </p>
                    <p className="profile-name">
                      {profile.fName} {profile.lName}
                    </p>
                    <p className="profile-headline">{profile.headline}</p>
                  </div>
                    </div>
                  </div>
                </div>
                <h3 className="profile-aboutMe profile-title">About Me</h3>
                <p style={{ color: "#696969", fontSize: "1.1em" }}>
                  {profile.aboutMe}
                </p>
                <h3 className="profile-email profile-title">Email</h3>
                <p style={{ color: "#696969", fontSize: "1.1em" }}>
                  {profile.email}
                </p>
                <h3 className="profile-locality profile-title">Locality</h3>
                <p style={{ color: "#696969", fontSize: "1.1em" }}>
                  {profile.locality}
                </p>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <div className="container-fluid">
          <Loading />
        </div>
      )}
    </>
  )
}
