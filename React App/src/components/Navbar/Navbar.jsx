import './Navbar.css';
import { NavLink } from 'react-router-dom';

const Navbar = () => {

  const LogoutHandler = () => {
    localStorage.setItem("username", "");
    localStorage.setItem("token", "")
  }

  return (
    <div style={{position:'sticky', top:'0'}} className="container">
      <div className="row">
        <div className="col navbar-container">
          <div className="logo">Milo</div>

          <NavLink exact to="/feed" className="main-nav" activeClassName="main-nav-active">
          <div className="home"><img style={{width:'3.5em'}} classname="nav-img" src="/images/home.png" alt=""/>Home</div>
          </NavLink>
          <NavLink exact to="/hashfeed" className="main-nav" activeClassName="main-nav-active">
        <div className="hashtag"><img style={{width:'3.5em'}} classname="nav-img" src="/images/hashtag.png" alt=""/>Hash Tag</div>
          </NavLink>
          <NavLink exact to="/eventfeed" className="main-nav" activeClassName="main-nav-active">
          <div className="events"><img style={{width:'3.5em'}} classname="nav-img" src="/images/e.png" alt=""/>Events</div>
          </NavLink>
          <NavLink exact to="/promotionfeed" className="main-nav" activeClassName="main-nav-active">
          <div className="blurb"><img style={{width:'3.5em'}} classname="nav-img" src="/images/arrow.png" alt=""/>Blurb</div></NavLink>
          <NavLink exact to="/mycontent" className="main-nav" activeClassName="main-nav-active">
          <div className="blurb"><img style={{width:'3.5em'}} classname="nav-img" src="/images/profile.png" alt=""/>My Content</div></NavLink>
          <NavLink exact to="/myprofile" className="main-nav" activeClassName="main-nav-active">
          <div className="blurb"><img style={{width:'3.5em'}} classname="nav-img" src="/images/setting.png" alt=""/>My Profile</div></NavLink>
          <NavLink exact to="/login" className="main-nav" activeClassName="main-nav-active">
          <div className="blurb" onClick={LogoutHandler}><img style={{width:'3.5em'}} classname="nav-img" src="/images/logout.png" alt=""/>Log Out</div>        
          </NavLink>
      </div>
      </div>
    </div>
  );
}

export default Navbar;