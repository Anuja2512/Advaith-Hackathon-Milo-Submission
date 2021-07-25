import React from 'react';
import '../ParticularPost/AddComment.css';
import '../ParticularPost/ParticularPost.css';

export default function Comment(props) {

    const comment = props.comment;

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
                    <div className="container container-fluid"><p className="post-comment" style={{color:'#696969', fontSize:'1.2em'}}>{comment.comment}</p></div>
                </div>
            </div>
    )
}
