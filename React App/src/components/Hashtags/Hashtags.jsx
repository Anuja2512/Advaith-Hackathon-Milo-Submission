import {React, useState, useEffect} from 'react';
import axios from "axios";
import { getURL, getUsername, getToken } from "../../utils/index";
import Hashtag from './Hashtag';
import NewHashtag from './NewHashtag';
import './Hashtags.css';
import { Button } from 'react-bootstrap';

export default function Hashtags() {

    const token = getToken();
    const username = getUsername();
    const url = getURL();
    const [screenReady, setScreenReady] = useState(false);

    const [hashtags, setHashtags] = useState([]);
    const [hashText, setHashText] = useState("")
    const [initialString, setInitialString] = useState("")

    useEffect(
        () => {
          axios({
            method: "GET",
            url:
              url + "/api/hashtag/"+username,
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
              setHashtags(data.value)
              let thatString = ""
              for(let i=0; i<data.value.length; i++)
              {
                  thatString += " " + data.value[i]
                  setInitialString(thatString)
                  setHashText(thatString)
              }
              setScreenReady(true)
            })
            .catch((err) => {
              alert("Something went wrong!");
              window.location = "/feed"
            });
        }, []
      )

      const [formSubmitted, setFormSubmitted] = useState(false)

      let styledHashes = [];

      for(let i=0; i<hashtags.length; i++)
      {
        styledHashes.push(<Hashtag key={i+1} tag={hashtags[i]} />)
      }

      const submitHandler = (e) => {
          e.preventDefault();
          setFormSubmitted(true)
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
          const thatURL = url + "/api/hashtag/"+username
          axios.put(
            thatURL,
            {
                "value": hashArray
            },
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
                      window.location = '/hashfeed'
                  }
                  alert("Hashtags updated successfully.")
                  window.location = "/hashfeed"
                  }
            }
          )
          .catch(
              err => {
                  alert("Some error occurred")
                  window.location = '/hashfeed'
                  return 0;
                  }
          );
      }

      const changeHandler = (e) => {
          setHashText(e.target.value)
      }


    return (
        <>
        <div className="container-fluid hashtags-container" style={{position:'sticky', top:'3%'}}>
          <div style={{textAlign:'center'}}>
          <img style={{width:'15em'}} src="/images/Hash-Tag.png" alt=""/></div>
          <br />
            <p style={{color:'#B76FFF', margin: "4% auto", fontWeight: "500"}}>All Hashtags</p>
            <div className="container-fluid">
                {styledHashes.map((item, index) => (item))}
            </div>
            <br/><br/>
            <div className="container-fluid">
                <form onSubmit={submitHandler}>
                    <textarea className="add-hashtag" name="newTags" placeholder="Add New Hashtags" defaultValue={initialString} onChange={changeHandler}></textarea><br />
                    <Button 
              style={{
                backgroundColor:"#B76FFF",
                borderRadius:'2rem',
                width:'100%',
                marginTop: '5%',
                outline:'none',
                border:'none'
              }}
              disabled={formSubmitted}
              as="input" type="submit" value="Update" />{' '}
                </form>
            </div>
        </div>
        </>
    )
}
