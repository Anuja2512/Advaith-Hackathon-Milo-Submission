import {React, useState, useEffect} from 'react';
import axios from "axios";
import { getURL, getUsername, getToken } from "../../utils/index";
import { Button } from 'react-bootstrap';
import '../ParticularPost/AddComment.css';

export default function AddComment(props) {

    const token = getToken();
    const username = getUsername();
    const url = getURL();

    const [comment, setComment] = useState("")

    const commentHandler = (e) => {
        setComment(e.target.value)
    }
    const [formSubmitted, setFormSubmitted] = useState(false)

    const [timeEpoch, setTimeEpoch] = useState("")
    
    useEffect(()=>{
        setTimeEpoch(''+Math.floor(Number(Date.now())/1000.0))
    }, [])

    const addCommentHandler = (e) => {
        e.preventDefault()
        setFormSubmitted(true)
        const theURL = url +"/api/eventcomments"
        const body = {
            "username": username,
            "comment": comment,
            "eventID": props.id,
            "timeEpoch": timeEpoch
          }

        axios.post(
            theURL,
            body,
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
                    if(data.status === 500)
                    {
                        alert(data.message);
                        window.location = "/event/"+props.id
                    }
                    alert("Comment Added Successfully")
                    window.location = "/event/"+props.id
                    }
            }
            )
            .catch(
                err => {
                    alert("Some error occurred")
                    window.location = "/event/"+props.id
                    return 0;
                    }
            );

    }

    return (
        <div className="container-fluid">
            <form onSubmit={addCommentHandler}>
                <div className="form-group add-comment-container">
                <label style={{color:'#B76FFF'}} htmlFor="comments" className="form-label">Add a Comment</label>
                <textarea style={{padding:'1% 2.5%',fontSize:'1.2em'}} onChange={commentHandler} className="form-control" id="exampleFormControlTextarea1" rows="3" placeholder="Critique, Opinion or +1. What's it gonna be?" required className="form-control comment-textarea" ></textarea>
                <br />
                <Button 
              className="btn btn-primary post-btn"
              style={{
                backgroundColor:"#D0A1FF",
                borderRadius:'1rem',
                outline:'none',
                border:'none'
              }}
              disabled={formSubmitted}
              as="input" type="submit" value="Comment" />{' '}
                </div>
            </form>
        </div>
    )
}
