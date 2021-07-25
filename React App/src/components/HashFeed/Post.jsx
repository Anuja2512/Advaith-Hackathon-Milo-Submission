import React from 'react'

export default function Post(props) {

    const data = props.data

    let hashTags = ""
    for(let i=0; i<data.hashtags.length; i++)
    {
        hashTags += " "+data.hashtags[i];
    }

    const postClickHandler = (e) => {
        window.location = "/post/"+data.key
    }

    return (
        <div className="container-fluid post" onClick={postClickHandler}>
            <div className="row">
                <div className="col-lg-3 col-md-3 col-sm-3 prof-pic-container">
                    { data.user.profilePicture.length > 0 ? <img src={data.user.profilePicture} className="img img-fluid post-profile-pic" alt="profilepicture"/> : <img src="/user.png" className="img img-fluid post-profile-pic" alt="profilepicture"/> }
                </div>
                <div className="col-lg-9 col-md-9 col-sm-9 user-info">
                    <h4 className="name">{data.user.fName} {data.user.lName}</h4>
                    <h4 className="username">{data.user.username}</h4>
                    <h5 className="headline">{data.user.headline}</h5>
                </div>
            </div>
            <div className="container-fluid post-container">
                <p className="post-data">
                    {data.data}
                </p>
                { data.imageLink.length > 0 ? <div className="post-pic-container"><img style={{width:'100%'}} src={data.imageLink} className="img-fluid post-pic" alt="postpicture"/></div> : null}
                <p className="post-hashtag">{hashTags}</p>
            </div>
        </div>
    )
}
