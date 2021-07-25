import React from 'react'

export default function Error404() {

    return (
        <div className="text-center" style={{backgroundColor: "#AFF1E4", height: "100%", width: "100%", paddingBottom: "15%"}}>
            
            <h1 style={{color: "#297F6C", paddingTop: "12%", fontSize: "4.5rem"}}>ERROR</h1>
            <div className="container container-fluid"><img className="img img-fluid" src="https://media.giphy.com/media/UoeaPqYrimha6rdTFV/giphy.gif" alt="lost" class="image"/>   
            </div><br /><h2 style={{color: "#297F6C"}}>Let's go <a href="/">Back</a></h2>
        </div>
    )
}
