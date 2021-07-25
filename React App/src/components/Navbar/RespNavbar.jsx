import {React, useState, useEffect} from 'react';
import './Navbar.css';
import './Resp-Navbar.css';
import { NavLink } from 'react-router-dom';

const RespNavbar = () => {

  const [showCollapse, setShowCollapse] = useState(false)

  const collapseHandler = () => {
    if(showCollapse === true)
    {
      setShowCollapse(false)
    }
    else
    {
      setShowCollapse(true)
    }
  }

  const LogoutHandler = () => {
    localStorage.setItem("username", "");
    localStorage.setItem("token", "")
  }

  return (
    <>
      <div className=" container-fluid g-0 position-sticky light-bg2 resp-scr" style={{margin: "0 auto", padding: "0 ",top: "0", width: "100%", zIndex: "100",borderRadius:'0'}}>
      <div className="row g-0 ">
        <div className="col-3">
          <p className="logo2">Milo</p>
        </div>
        <div className="col-6">
        </div>
        <div className="col-3 text-right float-right">
        <button className="logo2" onClick={collapseHandler}><div className="container container-fluid text-center align-items-right text-right float-right" style={{fontSize: "2.5rem"}}>≡</div></button>
        </div>
      </div>
      {showCollapse ? <div className="container container-fluid" style={{width:'50%'}}>
      <NavLink exact to="/feed" className="main-nav" activeClassName="main-nav-active">
          <div className="home"><img style={{width:'3.2em'}} classname="nav-img" src="/images/home.png" alt=""/>Home</div>
          </NavLink>
          <NavLink exact to="/hashfeed" className="main-nav" activeClassName="main-nav-active">
        <div className="hashtag"><img style={{width:'3.2em'}} classname="nav-img" src="/images/hashtag.png" alt=""/>Hash Tag</div>
          </NavLink>
          <NavLink exact to="/eventfeed" className="main-nav" activeClassName="main-nav-active">
          <div className="events"><img style={{width:'3.2em'}} classname="nav-img" src="/images/e.png" alt=""/>Events</div>
          </NavLink>
          <NavLink exact to="/promotionfeed" className="main-nav" activeClassName="main-nav-active">
          <div className="blurb"><img style={{width:'3.2em'}} classname="nav-img" src="/images/arrow.png" alt=""/>Blurb</div></NavLink>
          <NavLink exact to="/mycontent" className="main-nav" activeClassName="main-nav-active">
          <div className="blurb"><img style={{width:'3.2em'}} classname="nav-img" src="/images/profile.png" alt=""/>My Content</div></NavLink>
          <NavLink exact to="/myprofile" className="main-nav" activeClassName="main-nav-active">
          <div className="blurb"><img style={{width:'3.2em'}} classname="nav-img" src="/images/setting.png" alt=""/>My Profile</div></NavLink>
          <NavLink exact to="/login" className="main-nav" activeClassName="main-nav-active">
          <div className="blurb" onClick={LogoutHandler}><img style={{width:'3.5em'}} classname="nav-img" src="/images/logout.png" alt=""/>Log Out</div>        
          </NavLink>
      </div> : null}
      </div>
      
      
   </>
  )
};

export default RespNavbar;