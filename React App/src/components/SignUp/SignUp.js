import {React, useState, useEffect} from 'react';
import { Formik, Form } from 'formik';
import TextField from './TextField';
import * as Yup from 'yup';
import axios from "axios";
import { getURL, getUsername } from "../../utils/index";
import './SignUp.css';
import { Button } from 'react-bootstrap';
import '../Login/Login.css';
import Loading from '../Loading/Loading'

const FormData = require('form-data');

const SignUp = () => {

  useEffect(()=>{
    try{
      if(getUsername().length>0)
      {
        window.location="/feed"
      }
    }
    catch{
    }
  })

  const [allowedLocation, setAllowedLocation] = useState(false)
  useEffect(()=>{
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
          setAllowedLocation(true)
        }
    });
  }, [])

  const [profilePicture, setProfilePicture] = useState();
  const [formSubmitted, setFormSubmitted] = useState(false)

  const regHandler = (vals) => {

    setFormSubmitted(true)
    if(profilePicture.type!=="image/png" && profilePicture.type!=="image/jpg" && profilePicture.type!=="image/jpeg")
    {
      alert("Invalid File-Type. Try Again.");
      window.location = "/signup";
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
        const thatURL = getURL() + "/api/signup"
        axios.post(
          thatURL,
          vals,
          {
            headers: {
              'Content-Type': 'application/json'
            }
          }
        ).then(
          response => {
              if(response.status === 200){
                const data = response.data;
                if(data.status === 201)
                {
                  localStorage.setItem('username', data.username);
                  localStorage.setItem('token', data.token);
                  alert("Successfully Signed Up")
                  window.location = '/feed'
                }
                if(data.status === 409)
                {
                  alert(data.message);
                  window.location = '/login'
                }
              }
          }
        )
        .catch(
          err => {
            alert("Some error occurred")
            console.log(err);
            window.location = '/signup'
            return 0;
          }
        );
			})
			.catch((error) => {
        alert("Some error occurred")
        console.log(error);
        window.location = '/signup'
        return 0;
			});
  }

  const validate = Yup.object({
    fName: Yup.string()
      .max(15, 'Must be 15 characters or less')
      .required('Required'),
    lName: Yup.string()
      .max(20, 'Must be 20 characters or less')
      .required('Required'),
    username: Yup.string()
      .max(15, 'Must be 15 characters or less')
      .required('Username is required'),
    headline: Yup.string()
      .max(70, 'Must be 70 characters or less'),
    aboutMe: Yup.string()
      .max(250, 'Must be 250 characters or less'),
    email: Yup.string()
      .email('Email is invalid')
      .required('Email is required'),
    password: Yup.string()
      .min(8, 'Password must be at least 8 charaters')
      .required('Password is required'),
    confirmPassword: Yup.string()
      .oneOf([Yup.ref('password'), null], 'Password must match')
      .required('Confirm password is required'),
  })
const fileSelectedHandler = (e) => {
  setProfilePicture(e.target.files[0]);
}
  return (
    <>
    {allowedLocation ?
    <div className="su-page container g-0 mw-100">
    <div className="su-form-container g-0 row blur-container">
      <div className="col-lg-4 col-md-12 col-sm-12">
        <img className="img-log" src="../../images/Login-image.png" alt="SignUp"></img>
      </div>
      <div className="su-form col-lg-8 col-md-12 col-sm-12">
          <div className="su-header">Milo</div>
          <div className="su-form-bottom">
    <Formik
      initialValues={{
        fName: '',
        lName: '',
        username: '',
        headline: '',
        aboutMe: '',
        email: '',
        password: '',
        locality: '',
        confirmPassword: ''
      }}
      validationSchema={validate}
      onSubmit={values => {
        regHandler(values);
      }}
    >
      {formik => (
          <Form>
          <div className="container">
          <div className="row">
              <div className="col-lg-6 col-md-6 col-sm-12">
                <TextField className="textbox" label="First Name" name="fName" type="text" />
                
              </div>
              <div className="col-lg-6 col-md-6 col-sm-12">
                <TextField className="textbox" label="Last Name" name="lName" type="text" />
              </div>
              <div className="col-lg-6 col-md-6 col-sm-12">
                <TextField className="textbox" label="Username" name="username" type="text" />
              </div>
              <div className="col-lg-6 col-md-6 col-sm-12">
                <TextField className="textbox" label="Email" name="email" type="email" />
              </div>
              <div className="col-lg-12 col-md-12 col-sm-12">
                <TextField style={{padding:'2%'}} className="textbox" label="Headline" name="headline" type="text" />
              </div>
              <div className="col-lg-12 col-md-12 col-sm-12">
                <TextField style={{padding:'2.5%'}} className="textbox" label="About Me" name="aboutMe" type="text" />
              </div>
              
              <div className="col-lg-12 col-md-12 col-sm-12">
                <TextField style={{padding:'2.5%'}} className="textbox" label="Locality" name="locality" type="text" />
              </div>
              <div className="col-lg-6 col-md-6 col-sm-12">
              <TextField className="textbox" label="Password" name="password" type="password" />
              </div>
              <div className="col-lg-6 col-md-6 col-sm-12">
              <TextField className="textbox" label="Confirm Password" name="confirmPassword" type="password" />
              </div>
            <div className="row">
            <label className="su-label" htmlFor="profilePicture">Profile Picture</label><br/>
            <input name="profilePicture" type="file" onChange={fileSelectedHandler} accept="image/png image/jpeg image/jpg" required></input>
            </div>
            </div>
            <div style={{textAlign:'center'}}>
            <Button 
              style={{
                backgroundColor:"#B76FFF",
                borderRadius:'2rem',
                width:'50%',
                marginTop: '5%',
                outline:'none',
                border:'none'
              }}
              as="input" type="submit" disabled={formSubmitted} value="Sign Up" />{' '}
              </div>
            </div>
          </Form>
      )}
    </Formik>
    </div>
    </div>
    </div>
    </div> : <Loading/>}
    </>
  )
}
export default SignUp;