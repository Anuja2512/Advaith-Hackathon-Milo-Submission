import {React, useState, useEffect} from 'react';
import { Formik, Form } from 'formik';
import TextField from './TextField';
import * as Yup from 'yup';
import axios from "axios";
import { getURL, getUsername, getToken } from "../../utils/index";
import Loading from '../Loading/Loading';
import Navbar from '../Navbar/Navbar';
import './UpdateProfile.css';

const FormData = require('form-data');

const UpdateProfile = () => {

    const token = getToken();
    const username = getUsername();
    const url = getURL();

  const [profilePicture, setProfilePicture] = useState({type: 'empty'});
  const [screenReady, setScreenReady] = useState(false);
  const [profile, setProfile] = useState();
  const [allowedLocation, setAllowedLocation] = useState(false)

  useEffect(
    () => {
      axios({
        method: "GET",
        url:
          url + "/api/profile/"+username,
        headers: {
          "Content-Type": "application/json",
          Authorization: `${token}`,
        },
      })
        .then((response) => {
          const data = response.data;
          if(data.status === 401)
          {
            alert("Your Session has expired.")
            window.location = "/login"
          }
          if(data.status === 404)
          {
            alert("Profile does not exist");
            window.location = "/feed"
          }
          setProfile(data);
          setScreenReady(true)
        })
        .catch((err) => {
          alert("Somthing went wrong!");
        });
    }, []
  )

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

  const regHandler = (vals) => {
    if(profilePicture.type === "empty")
    {
        vals['profilePicture'] = profile.profilePicture
        const thatURL = getURL() + "/api/profile/"+username
        axios.put(
            thatURL,
            vals,
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
                if(data.status === 404)
                {
                    alert(data.message);
                    window.location = '/updateprofile'
                }
                alert("Profile updated successfully.")
                window.location = "/myprofile"
                }
            }
        )
        .catch(
            err => {
            alert("Some error occurred")
            console.log(err);
            window.location = '/myprofile'
            return 0;
            }
        );
    }
    else
    {
        if(profilePicture.type!=="image/png" && profilePicture.type!=="image/jpg" && profilePicture.type!=="image/jpeg")
        {
          alert("Invalid File-Type. Try Again.");
          window.location = "/updateprofile";
          return 0;
        }
    
        //upload that image and get a link
        const formData = new FormData();
        formData.append('file', profilePicture);
        fetch(
                getURL()+'/api/uploadimage',
                {
                    method: 'POST',
                    body: formData,
                }
            )
            .then((response) => response.json())
                .then((result) => {
            vals['profilePicture'] = result.link
            const thatURL = getURL() + "/api/profile/"+username
            axios.put(
              thatURL,
              vals,
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
                    if(data.status === 404)
                    {
                        alert(data.message);
                        window.location = '/updateprofile'
                    }
                    alert("Profile updated successfully.")
                    window.location = "/myprofile"
                    }
              }
            )
            .catch(
                err => {
                    alert("Some error occurred")
                    console.log(err);
                    window.location = '/myprofile'
                    return 0;
                    }
            );
                })
                .catch((error) => {
            alert("Some error occurred")
            console.log(error);
            window.location = '/updateprofile'
            return 0;
                });
    }
  }

  const validate = Yup.object({
    fName: Yup.string()
      .max(15, 'Must be 15 characters or less')
      .required('Required'),
    lName: Yup.string()
      .max(20, 'Must be 20 characters or less')
      .required('Required'),
    headline: Yup.string()
      .max(70, 'Must be 70 characters or less'),
      aboutMe: Yup.string()
      .max(250, 'Must be 250 characters or less'),
    email: Yup.string()
      .email('Email is invalid')
      .required('Email is required')
  })

const fileSelectedHandler = (e) => {
  setProfilePicture(e.target.files[0]);
}

  return (<>
     {screenReady && allowedLocation ? <Formik
      initialValues={{
        fName: profile.fName,
        lName: profile.lName,
        headline: profile.headline,
        aboutMe: profile.aboutMe,
        email: profile.email,
        locality: profile.locality,
        username: profile.username
      }}
      validationSchema={validate}
      onSubmit={values => {
        regHandler(values);
      }}
    >
      {formik => (
        <Form>
        <div className="fluid-container g-0 mw-100 bg" >
        <div className="row g-0 blur-container">
          <div className="col-lg-2 col-md-12 col-sm-12 profile-navbar">
            <Navbar/>
          </div>
          <div className="col-lg-9 col-md-9 col-sm-9 profile-container">
              <div style={{paddingLeft:'6%',position:'relative',height:'97%'}} className="light-bg light-cont row">
              <div className="col-lg-12 col-md-12 col-sm-12">
              <div className="pic-cont update-pic-cont" style={{textAlign:'center'}}>
            { profile.profilePicture.length > 0 ? <img
            style={{borderRadius: '50%',
              height:'170px',
              width:'170px',  transform: 'translateY(-40%)'
            }}
            src={profile.profilePicture} className="img img-fluid" alt="profilepicture"/> : <img src="/user.png" className="img img-fluid" alt="profilepicture" /> }
            </div></div>
              <div className="col-lg-6 col-md-12 col-sm-12">
            <TextField className="textbox update-field" label="First Name" name="fName" type="text" /></div>
            <div className="col-lg-6 col-md-12 col-sm-12">
            <TextField className="textbox update-field" label="Last Name" name="lName" type="text" /></div>
            <div className="col-lg-6 col-md-12 col-sm-12">
            <TextField className="textbox update-field" label="Headline" name="headline" type="text" /></div>
            <div className="col-lg-6 col-md-12 col-sm-12">
            <TextField className="textbox update-field" label="Username" name="username" type="text" disabled = {true}/></div>
            <div className="col-lg-12 col-md-12 col-sm-12">
            <TextField className="textbox" label="About Me" name="aboutMe" type="textarea" /></div>
            <div className="col-lg-6 col-md-12 col-sm-12">
            <TextField className="textbox update-field" label="Email" name="email" type="email" /></div>
            <div className="col-lg-6 col-md-12 col-sm-12">
            <TextField className="textbox update-field" label="Locality" name="locality" type="text" /></div>
            <label htmlFor="profilePicture">Profile Picture</label><br/>
            <input name="profilePicture" type="file" onChange={fileSelectedHandler} accept="image/png image/jpeg image/jpg"></input><br/><br/>
            
            <button style={{width:'auto', background:'none',right:'0'}} className="update-profile " type="submit"><img src="/images/save.svg" alt="Save Profile"/></button>
           
        </div>
        </div>
            </div>
        </div>
        </Form>
      )}
    </Formik> : <div className="container-fluid">
            <Loading/>
        </div>}</>
  )
}
export default UpdateProfile;
