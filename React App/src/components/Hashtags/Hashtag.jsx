import React from 'react';
import './Hashtags.css';

export default function Hashtag(props) {
    return (
            <div className="hashtag-btn" style={{margin: '1%'}}>{props.tag}</div>
    )
}

