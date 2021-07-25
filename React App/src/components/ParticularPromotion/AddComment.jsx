import {React, useState, useEffect} from 'react';
import axios from "axios";
import { getURL, getUsername, getToken } from "../../utils/index";
import Rating from '@material-ui/lab/Rating';
import Typography from '@material-ui/core/Typography';
import Box from '@material-ui/core/Box';
import { Button } from 'react-bootstrap';
import './Promotion.css';

export default function AddComment(props) {

    const token = getToken();
    const username = getUsername();
    const url = getURL();

    const [comment, setComment] = useState("")

    const commentHandler = (e) => {
        setComment(e.target.value)
    }
    const [formSubmitted, setFormSubmitted] = useState(false)

    const [value, setValue] = useState(2);


    const addCommentHandler = (e) => {
        e.preventDefault()
        setFormSubmitted(true)
        const theURL = url +"/api/ratings"
        const body = {
            "rate": value,
            "username": username,
            "promotionID": props.id,
            "feedback": comment
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
                        window.location = "/promotion/"+props.id
                    }
                    alert("Comment Added Successfully")
                    window.location = "/promotion/"+props.id
                    }
            }
            )
            .catch(
                err => {
                    alert("Some error occurred")
                    window.location = "/promotion/"+props.id
                    return 0;
                    }
            );
    }

    return (
        <div className="container-fluid">
            <form onSubmit={addCommentHandler}>
                <div className="form-group">
                <Box component="fieldset" mb={3} borderColor="transparent">
                    <Typography className="rating" component="legend">Give your Rating</Typography>
                    <Rating
                    name="simple-controlled"
                    value={value}
                    onChange={(event, newValue) => {
                        setValue(newValue);
                    }}
                    />
                </Box>
                <label  style={{color:'#B76FFF'}} htmlFor="comments" className="form-label">Your Feedback</label>
                <textarea style={{padding:'1% 2.5%',fontSize:'1.2em'}} onChange={commentHandler} className="form-control" id="exampleFormControlTextarea1" rows="3" placeholder="A genuine feedback" className="form-control comment-textarea"  required></textarea>
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

