import {React, useState, useEffect} from 'react'
import './Login.css';
import { Formik, Form, Field, ErrorMessage } from "formik";
import * as Yup from "yup";
import axios from "axios";
import { getURL, getUsername } from "../../utils/index";
import { Button } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import Loading from '../Loading/Loading'

const signInSchema = Yup.object().shape({
  username: Yup.string().required("Username is required"),
  password: Yup.string().required("Password is required")
});

const initialValues = {
  username: "",
  password: ""
};

const Login = () => {

  const [formSubmitted, setFormSubmitted] = useState(false)

  const loginHandler = (e) => {
 
    const thatURL = getURL() + "/api/login";
    setFormSubmitted(true)
  
    axios
      .post(
        thatURL,
        {
          username: e.username,
          password: e.password,
        },
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      )
      .then((response) => {
        if (response.status === 200) {
          const data = response.data;
          console.log(data);
          if (data.status === 404) {
            alert(data.message);
            window.location = "/login";
            return 0;
          }
          if (data.status === 403) {
            alert(data.message);
            window.location = "/login";
            return 0;
          }
          localStorage.setItem("username", data.username);
          localStorage.setItem("token", data.token);
          alert("Successfully Logged In")
          window.location = "/feed";
        }
      })
      .catch((err) => {
        alert("Server Seems to be down. Please try later. We got this.");
        window.location = '/login'
      });
  };

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
  

  return (
    <>
    {allowedLocation ?
    <div className="fluid-container mw-100 page">
      <div className="form-container row blur-container g-0">
        <div className="col-lg-4 col-md-10 col-sm-10">
          <img className="img-log" src="../../images/Login-image.png" alt="Login"></img>
        </div>
        <div className="form col-lg-8 col-md-12 col-sm-12">
          <div className="header">Milo</div>
          <div className="form-bottom">
    <Formik
      initialValues={initialValues}
      validationSchema={signInSchema}
      onSubmit={(values) => {
          loginHandler(values);
        }}
    >
      
      {(formik) => {
        const { errors, touched, isValid, dirty } = formik;
        return (
          <div>
            <Form>
              <div>
                <label className="d-block label" htmlFor="username">Username</label>
                <Field
                  type="text"
                  name="username"
                  id="username"
                  className={
                    errors.username && touched.username ? "input-error" : null
                  }
                />
                <ErrorMessage name="username" component="span" className="error" />
              </div>

              <div className="form-row">
                <label htmlFor="password" className="d-block label">Password</label>
                <Field
                  type="password"
                  name="password"
                  id="password"
                  className={
                    errors.password && touched.password ? "input-error" : null
                  }
                />
                <ErrorMessage
                  name="password"
                  component="span"
                  className="error"
                />
              </div>
              <div style={{textAlign:'right'}}>
                <a className="signUp" href="SignUp">Not a Milo User yet? Sign Up!</a>
              </div>
            <div style={{textAlign:'center'}}>
              <Button 
              className={!(dirty && isValid) ? "disabled-btn" : ""}
              disabled={!(dirty && isValid)}
              style={{
                backgroundColor:"#B76FFF",
                borderRadius:'2rem',
                width:'50%',
                marginTop: '10%',
                outline:'none',
                border:'none'
              }}
              as="input" type="submit" value="Login" />{' '}
              </div>
            </Form>
          </div>
        );
      }}
    </Formik>
    </div>
    </div>
    </div>
    </div> : <Loading/>}
    </>
  );
};

export default Login;
