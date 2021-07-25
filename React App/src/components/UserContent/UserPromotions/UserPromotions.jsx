import {React, useState, useEffect} from 'react';
import axios from "axios";
import { getURL, getUsername, getToken } from "../../../utils/index";
import Post from './Post';
import Loader from "react-loader-spinner";
import "react-loader-spinner/dist/loader/css/react-spinner-loader.css";

export default function UserPromotions() {

    const token = getToken();
    const username = getUsername();
    const url = getURL();

    const [locationState, setLocationState] = useState([]);
    const [screenReady, setScreenReady] = useState(false);
    const [event, setEvent] = useState([])

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
            axios({
                method: "GET",
                url:
                  url + "/api/userpromotions/"+username,
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
                  console.log(data)
                  setScreenReady(true)
                })
                .catch((err) => {
                  alert("Something went wrong!");
                });
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

    return (
        <>
        {screenReady&&allowedLocation ? <div className="container container-fluid">
            {thePosts.map((curele) => curele)}
        </div> : <div className="container container-fluid text-center"><Loader style={{margin: "3% auto"}} type="Bars" color="#B982FC" height={60} width={60} /></div>}
    </>
    )
}