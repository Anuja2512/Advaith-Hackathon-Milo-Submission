import {React, useState, useEffect} from 'react';
import './AddComment.css'
import './ParticularPost.css'

export default function Comment(props) {

    const comment = props.comment;

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

    return (
            <div className="container-fluid">
                <div className="row">
                    <div className="col-lg-3 col-md-3 col-sm-3 pap-prof-pic-container dp-contain">
                    { comment.user.profilePicture.length > 0 ? <img src={comment.user.profilePicture} className="rounded post-profile-pic float-right" alt="profilepicture" /> : <img src="/user.png" className="img img-fluid rounded post-profile-pic" alt="profilepicture" /> }
                    </div>
                    <div style={{fontSize:'90%'}} className="col-lg-9 col-md-9 col-sm-9 pap-user-info user-comment">
                        <p className="name">{comment.user.fName} {comment.user.lName}</p>
                        <p className="username">@{comment.user.username}</p>   
                    </div>
                    <p className="post-comment" style={{color:'#696969', fontSize:'1.2em'}}>{comment.comment}</p>
                </div>
            </div>
    )
}