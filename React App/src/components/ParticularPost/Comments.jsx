import React from 'react'
import Comment from './Comment'

export default function Comments(props) {

    const id = props.id
    const data = props.data
    const comments = data.comments

    const theComments = []
    for(let i=0; i<comments.length; i++)
    {
        theComments.push(<Comment key={i+1} comment={comments[i]}/>);
    }

    return (
        <div className="container container-fluid">
            <br />
            <h3 style={{fontSize:'1.2em', color:'#696969'}}>Comments: {data.comments.length}</h3>
            <br />
            {theComments.map((curele) => {
                return curele
            })}
        </div>
    )
}
