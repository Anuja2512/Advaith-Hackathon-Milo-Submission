import React from 'react';
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";

import './App.css';
import Login from './components/Login/Login';
import SignUp from './components/SignUp/SignUp'
import Profile from './components/Profile/Profile'
import UpdateProfile from './components/UpdateProfile/UpdateProfile'
import ParticularPost from './components/ParticularPost/ParticularPost';
import ParticularEvent from './components/ParticularEvent/ParticularEvent';
import ParticularPromotion from './components/ParticularPromotion/ParticularPromotion';
import UserProfile from './components/UserProfile/UserProfile';
import Feed from './components/Feed/Feed';
import HashFeed from './components/HashFeed/HashFeed';
import EventFeed from './components/EventFeed/EventFeed';
import PromotionFeed from './components/PromotionFeed/PromotionFeed';
import UserContent from './components/UserContent/UserContent';
import LandingPage from './components/LandingPage/LandingPage';
import Error404 from './components/Error404/Error404'
function App() {
  return (
    <div className="App">
      <Router>
        <Switch>
          <Route exact path="/" component={LandingPage}></Route>
          <Route exact path="/login" component={Login}></Route>
          <Route exact path="/signup" component={SignUp}></Route>
          <Route exact path="/myprofile" component={Profile}></Route>
          <Route exact path="/updateprofile" component={UpdateProfile}></Route>
          <Route exact path="/post/:id" component={ParticularPost}></Route>
          <Route exact path="/event/:id" component={ParticularEvent}></Route>
          <Route exact path="/promotion/:id" component={ParticularPromotion}></Route>
          <Route exact path="/profile/:username" component={UserProfile}></Route>
          <Route exact path="/feed" component={Feed}></Route>
          <Route exact path="/hashfeed" component={HashFeed}></Route>
          <Route exact path="/eventfeed" component={EventFeed}></Route>
          <Route exact path="/promotionfeed" component={PromotionFeed}></Route>
          <Route exact path="/mycontent" component={UserContent}></Route>
          <Route component={Error404}></Route>
        </Switch>
      </Router>
    </div>
  );
}
export default App;