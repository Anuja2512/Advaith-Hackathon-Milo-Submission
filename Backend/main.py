from fastapi import FastAPI, File, UploadFile, Response, Header
from fastapi.responses import FileResponse, RedirectResponse
from typing import Optional
from deta import Deta
from pydantic import BaseModel
import hashlib
import jwt
import uuid
import json
from datetime import datetime, timedelta
from fastapi import File, UploadFile
from fastapi.responses import HTMLResponse, StreamingResponse
from fastapi.middleware.cors import CORSMiddleware
from math import radians, cos, sin, asin, sqrt
import time
import pyrebase

# Sensitive Data removed...

serviceAccount = {
  
}

config = {
    "apiKey": "",
    "authDomain": "",
    "projectId": "",
    "databaseURL": "",
    "storageBucket": "",
    "serviceAccount": serviceAccount
  };

firebase_storage = pyrebase.initialize_app(config)
storage = firebase_storage.storage()


# pydantic to declare body of put or post
app = FastAPI()
a = ""
deta = Deta(a+"")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

def common_member(a, b):
    a_set = set(a)
    b_set = set(b)
    if len(a_set.intersection(b_set)) > 0:
        return(True) 
    return(False)

def validateToken(token):
    try:
        validation = jwt.decode(token, '', algorithms="HS256")
        return True
    except:
        return False
    
    
def haversine(lon1, lat1, lon2, lat2):
    """
    Calculate the great circle distance between two points 
    on the earth (specified in decimal degrees)
    """
    # convert decimal degrees to radians 
    lon1, lat1, lon2, lat2 = map(radians, [lon1, lat1, lon2, lat2])

    # haversine formula 
    dlon = lon2 - lon1 
    dlat = lat2 - lat1 
    a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
    c = 2 * asin(sqrt(a)) 
    r = 6371 # Radius of earth in kilometers. Use 3956 for miles
    return c * r

def isInside(center_point, test_point, radius):
    lat1 = center_point[0]['lat']
    lon1 = center_point[0]['lng']
    lat2 = test_point[0]['lat']
    lon2 = test_point[0]['lng']
    
    a = haversine(lon1, lat1, lon2, lat2)
    
    if a <= radius:
        return True
    else:
        return False
    
    
@app.get("/")
def read_root():
    return {"message": "Milo - The Next Gen Social Media"}

class User(BaseModel):
    fName: str
    lName: str
    username: str
    email: str
    headline: str
    aboutMe: str
    password: str
    profilePicture: str
    locality: str

@app.post("/api/signup")
def signup(user: User):
    
    userdb = deta.Base("Milo_User")
    
    #hash the password
    user.password = hashlib.sha256(user.password.encode()).hexdigest()
    
    createUser = {
        "fName": user.fName,
        "lName": user.lName,
        "username": user.username,
        "email": user.email,
        "headline": user.headline,
        "aboutMe": user.aboutMe,
        "password": user.password,
        "profilePicture": user.profilePicture,
        "locality": user.locality
    }
    
    try:
        newuser = userdb.insert(createUser, user.username)
        hashtagdb = deta.Base("Milo_Hashtag")
        newHashInstance = hashtagdb.insert([], user.username)
    except:
        return({
            "status": 409,
            "message": "Username already exists."
        })
        
    JWT_SECRET = ''
    JWT_ALGORITHM = 'HS256'
    JWT_EXP_DELTA_SECONDS = 2628000
    payload = {'exp': datetime.utcnow() + timedelta(seconds=JWT_EXP_DELTA_SECONDS)}        
    jwt_token = jwt.encode(payload, JWT_SECRET, JWT_ALGORITHM)
    
    return({
        "status": 201,
        "message": "User created successfully.",
        "token": jwt_token,
        "key": user.username,
        "fName": user.fName,
        "lName": user.lName,
        "username": user.username,
        "email": user.email,
        "headline": user.headline,
        "aboutMe": user.aboutMe,
        "profilePicture": user.profilePicture,
        "locality": user.locality
    })
    
class Login(BaseModel):
    username: str
    password: str
    
@app.post("/api/login")
def loginUser(login: Login):
    username = login.username
    password = login.password
    hashedPassword = hashlib.sha256(login.password.encode()).hexdigest()
    userdb = deta.Base("Milo_User")
    
    #check if username exists
    #theUser = next(userdb.fetch({"username": username}))
    theUser = userdb.fetch({"username": username}).items
    
    if len(theUser) == 0:
        return({
            "status": 404,
            "message": "Username does not exist."
        })
        
    theUser = theUser[0]
    
    #check password
    if theUser['password'] != hashedPassword:
        return({
            "status": 403,
            "message": "Password does not match."
        })
        
    #generate token
    JWT_SECRET = ''
    JWT_ALGORITHM = 'HS256'
    JWT_EXP_DELTA_SECONDS = 2628000
    payload = {'exp': datetime.utcnow() + timedelta(seconds=JWT_EXP_DELTA_SECONDS)}        
    jwt_token = jwt.encode(payload, JWT_SECRET, JWT_ALGORITHM)
    
    return({
        "status": 200,
        "message": "Successfully Logged In.",
        "token": jwt_token,
        "fName": theUser['fName'],
        "lName": theUser['lName'],
        "username": theUser['username'],
        "email": theUser['email']
    })
    
class UpdateProfile(BaseModel):
    fName: str
    lName: str
    email: str
    headline: str
    aboutMe: str
    profilePicture: str
    locality: str
    
@app.put("/api/profile/{username}")
def createproject(username: str, updateprofile: UpdateProfile, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    userdb = deta.Base("Milo_User")
    
    try:
        theUser = userdb.get(username)
        theUser['fName'] = updateprofile.fName
        theUser['lName'] = updateprofile.lName
        theUser['email'] = updateprofile.email
        theUser['headline'] = updateprofile.headline
        theUser['aboutMe'] = updateprofile.aboutMe
        theUser['profilePicture'] = updateprofile.profilePicture
        theUser['locality'] = updateprofile.locality
        theUser = userdb.put(theUser)
        del theUser['password']
        theUser['status'] = 200
        return theUser
    except:
        return({
            "status": 404,
            "message": "Profile Does not Exist"
        })
        
@app.get("/api/profile/{username}")
def getnotes(username: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    userdb = deta.Base("Milo_User")
    
    try:
        theUser = userdb.get(username)
        del theUser['password']
        return theUser
    except:
        return({
            "status": 404,
            "message": "Profile Does not Exist"
        })
        
# hashtags

@app.get("/api/hashtag/{username}")
def gethashtags(username: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    hashtagdb = deta.Base("Milo_Hashtag")
    
    try:
        theInstance = hashtagdb.get(username)
        if theInstance == None:
            return({
                "status": 404,
                "message": "Username Does not Exist"
            })
        return theInstance
    except:
        return({
            "status": 404,
            "message": "Username Does not Exist"
        })        
        
class Hashtags(BaseModel):
    value: list        
        
@app.put("/api/hashtag/{username}")
def gethashtags(hashtags: Hashtags, username: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    hashtagdb = deta.Base("Milo_Hashtag")
    
    try:
        theInstance = hashtagdb.get(username)
        if theInstance == None:
            return({
                "status": 404,
                "message": "Username Does not Exist"
            })
        theInstance['value'] = hashtags.value
        theInstance = hashtagdb.put(theInstance)
        return theInstance
    except:
        return({
            "status": 404,
            "message": "Username Does not Exist"
        })

# var firebaseConfig = {
#     apiKey: "AIzaSyDfnH7lODsYG8H0MndSRDu2ZIgoGOm2WE0",
#     authDomain: "milo-e0f55.firebaseapp.com",
#     projectId: "milo-e0f55",
#     storageBucket: "milo-e0f55.appspot.com",
#     messagingSenderId: "984725929981",
#     appId: "1:984725929981:web:6842c191962b593fc32394",
#     measurementId: "G-DEKZT55PQE"
#   };

@app.post("/api/uploadimage")
def uploadImage(file: UploadFile = File(...), Authorization: Optional[str] = Header(None)):
    
    #imageDrive = deta.Drive("Milo_Image")
    
    fileName = str(uuid.uuid4())
    fileExtension = file.filename.split(".")
    fileExtension = fileExtension[len(fileExtension)-1]
    fileName += "."+fileExtension
    
    imageStorage = storage.child(fileName)
    imageStorage.put(file.file)
    
    #imageDrive.put(name=fileName, data=file.file, content_type="image/"+fileExtension)
    
    return {
        "status": 200,
        "link": "https://milo-backend.deta.dev/api/getimage/"+fileName
    }
    
@app.get("/api/getimage/{imageLocation}")
def getImage(imageLocation: str, Authorization: Optional[str] = Header(None)):
    
    # if validateToken(Authorization) is False:
    #     return {
    #         "status": 401,
    #         "message": "Invalid Token"
    #     }
    
    #subjectDrive = deta.Drive("Milo_Image")
    try:
        # imageFile = subjectDrive.get(imageLocation)
        # imageExtension = imageLocation.split(".")[1]
        # return StreamingResponse(imageFile.iter_chunks(1024), media_type="image/"+imageExtension)
        imageFile = storage.child('/'+imageLocation);
        imageExtension = imageLocation.split(".")[1]
        return RedirectResponse(url = imageFile.get_url(token=None))
        #return StreamingResponse(imageFile.iter_chunks(1024), media_type="image/"+imageExtension)
    except:
        return({
            "status": 404,
            "message": "Image Does not Exist"
        })
        
#posts
        
# center_point = [{'lat': 18.6329585, 'lng': 73.8058887}]
# test_point = [{'lat': 18.6143027, 'lng': 73.7991246}]

# isInside(center_point, test_point, radius)

class Post(BaseModel):
    username: str
    data: str
    imageLink: str
    hashtags: list
    latitude: str
    longitude: str
    radius: str
    timeEpoch: str

@app.post("/api/posts")
def createpost(post: Post, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    username = post.username
    data = post.data
    imageLink = post.imageLink
    hashtags = post.hashtags
    center_point = [{'lat': post.latitude, 'lng': post.longitude}]
    radius = post.radius
    timeEpoch = post.timeEpoch
    
    postdb = deta.Base("Milo_Post")
    
    createPost = {
        "username": username,
        "data": data,
        "imageLink": imageLink,
        "hashtags": hashtags,
        "center_point": center_point,
        "radius": radius,
        "timeEpoch": timeEpoch
    }
    
    try:
        newPost = postdb.insert(createPost)
        return newPost
    
    except:
        return({
            "status": 500,
            "message": "Some Error Occurred."
        })
        
class Comment(BaseModel):
    username: str
    comment: str
    postID: str
    timeEpoch: str

@app.post("/api/comments")
def createcomment(commentInstance: Comment, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    username = commentInstance.username
    comment = commentInstance.comment
    postID = commentInstance.postID
    timeEpoch = commentInstance.timeEpoch
    
    commentdb = deta.Base("Milo_Comment")
    
    createComment = {
        "username": username,
        "comment": comment,
        "timeEpoch": timeEpoch,
        "postID": postID
    }
    
    try:
        newComment = commentdb.insert(createComment)
        return newComment
    except:
        return({
            "status": 500,
            "message": "Some Error Occurred."
        })
        
#events
class Event(BaseModel):
    title: str
    imageLink: str
    description: str
    username: str
    deadlineTimeEpoch: str
    venueLatitude: str
    venueLongitude: str
    radius: str
    
@app.post("/api/events")
def createEvent(event: Event, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    title = event.title
    imageLink = event.imageLink
    description = event.description
    username = event.username
    deadlineTimeEpoch = event.deadlineTimeEpoch
    radius = event.radius
    center_point = [{'lat': event.venueLatitude, 'lng': event.venueLongitude}]
    
    eventdb = deta.Base("Milo_Event")
    
    createEvent = {
        "username": username,
        "title": title,
        "imageLink": imageLink,
        "description": description,
        "deadlineTimeEpoch": deadlineTimeEpoch,
        "radius": radius,
        "center_point": center_point
    }
    
    try:
        newEvent = eventdb.insert(createEvent)
        return newEvent
    except:
        return({
            "status": 500,
            "message": "Some Error Occurred."
        })
        
#eventscomments
class EventComment(BaseModel):
    username: str
    comment: str
    eventID: str
    timeEpoch: str
    
@app.post("/api/eventcomments")
def createcomment(commentInstance: EventComment, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    username = commentInstance.username
    comment = commentInstance.comment
    eventID = commentInstance.eventID
    timeEpoch = commentInstance.timeEpoch
    
    commentdb = deta.Base("Milo_Event_Comment")
    
    createComment = {
        "username": username,
        "comment": comment,
        "timeEpoch": timeEpoch,
        "eventID": eventID
    }
    
    try:
        newComment = commentdb.insert(createComment)
        return newComment
    except:
        return({
            "status": 500,
            "message": "Some Error Occurred."
        })
        
        
class Promotion(BaseModel):
    title: str
    profession: str
    imageLink: str
    description: str
    latitude: str
    longitude: str
    radius: str
    username: str
    
@app.post("/api/promotions")
def createpromotion(promotion: Promotion, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    title = promotion.title
    profession = promotion.profession
    imageLink = promotion.imageLink
    description = promotion.description
    radius = promotion.radius
    username = promotion.username
    center_point = [{'lat': promotion.latitude, 'lng': promotion.longitude}]
    
    promotiondb = deta.Base("Milo_Promotion")
    
    createPromotion = {
        "title": title,
        "profession": profession,
        "imageLink": imageLink,
        "description": description,
        "radius": radius,
        "username": username,
        "center_point": center_point
    }
    
    try:
        newPromotion = promotiondb.insert(createPromotion)
        return newPromotion
    except:
        return({
            "status": 500,
            "message": "Some Error Occurred."
        })
        
class Rating(BaseModel):
    rate: str
    username: str
    promotionID: str
    feedback: str

@app.post("/api/ratings")
def createcomment(rating: Rating, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    rate = rating.rate
    username = rating.username
    promotionID = rating.promotionID
    feedback = rating.feedback
    
    ratingdb = deta.Base("Milo_Rating")
    
    createRating = {
        "username": username,
        "rate": rate,
        "promotionID": promotionID,
        "feedback": feedback
    }
    
    try:
        newRating = ratingdb.insert(createRating)
        return newRating
    except:
        return({
            "status": 500,
            "message": "Some Error Occurred."
        })
        
#update post radius
class UpdatePost(BaseModel):
    radius: str
    
@app.put("/api/updatepost/{key}")
def createproject(key: str, updatepost: UpdatePost, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    postdb = deta.Base("Milo_Post")
    
    try:
        thePost = postdb.get(key)
        thePost['radius'] = updatepost.radius
        thePost = postdb.put(thePost)
        return thePost
    except:
        return({
            "status": 404,
            "message": "Post Does not Exist"
        })
        
#update event data
class UpdateEvent(BaseModel):
    # title: str
    # imageLink: str
    # description: str
    # deadlineTimeEpoch: str
    radius: str
    
@app.put("/api/updateevent/{key}")
def createproject(key: str, event: UpdateEvent, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    eventdb = deta.Base("Milo_Event")
    
    try:
        theEvent = eventdb.get(key)
        # theEvent['title'] = event.title
        # theEvent['imageLink'] = event.imageLink
        # theEvent['description'] = event.description
        # theEvent['deadlineTimeEpoch'] = event.deadlineTimeEpoch
        theEvent['radius'] = event.radius
        theEvent = eventdb.put(theEvent)
        return theEvent
    except:
        return({
            "status": 404,
            "message": "Event Does not Exist"
        })
        
#update promotion
class UpdatePromotion(BaseModel):
    # title: str
    # profession: str
    # imageLink: str
    # description: str
    radius: str
    
@app.put("/api/updatepromotion/{key}")
def createproject(key: str, promotion: UpdatePromotion, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    promotiondb = deta.Base("Milo_Promotion")
    
    try:
        thePromotion = promotiondb.get(key)
        # thePromotion['title'] = promotion.title
        # thePromotion['imageLink'] = promotion.imageLink
        # thePromotion['description'] = promotion.description
        # thePromotion['profession'] = promotion.profession
        thePromotion['radius'] = promotion.radius
        thePromotion = promotiondb.put(thePromotion)
        return thePromotion
    except:
        return({
            "status": 404,
            "message": "Event Does not Exist"
        })
        
# delete post
@app.delete("/api/post/{key}")
def getproject(key: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    try:
        postdb = deta.Base("Milo_Post")
        postdb.delete(key)
        
        # delete all comments of the post
        commentdb = deta.Base("Milo_Comment")
        allComments = commentdb.fetch({"postID": key}).items
        commentList = []
        for a in allComments:
                commentList.append(a['key'])
        
        for cl in commentList:
            commentdb.delete(cl)
        
        return ({
            "status": 203,
            "message": "Deleted Successfully."
        })
        
    except:
        return({
            "status": 404,
            "message": "Post Does not Exist"
        })
        
#delete event
@app.delete("/api/event/{key}")
def getproject(key: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    try:
        eventdb = deta.Base("Milo_Event")
        eventdb.delete(key)
        
        # delete all comments of the event
        commentdb = deta.Base("Milo_Event_Comment")
        allComments = commentdb.fetch({"eventID": key}).items
        commentList = []
        for a in allComments:
                commentList.append(a['key'])
        
        for cl in commentList:
            commentdb.delete(cl)
        
        return ({
            "status": 203,
            "message": "Deleted Successfully."
        })
        
    except:
        return({
            "status": 404,
            "message": "Event Does not Exist"
        })
        
#delete promotion
@app.delete("/api/promotion/{key}")
def deletePromotion(key: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    try:
        promotiondb = deta.Base("Milo_Promotion")
        promotiondb.delete(key)
        
        # delete all ratings of the promotion
        commentdb = deta.Base("Milo_Rating")
        allComments = commentdb.fetch({"promotionID": key}).items
        commentList = []
        for a in allComments:
                commentList.append(a['key'])
        
        for cl in commentList:
            commentdb.delete(cl)
        
        return ({
            "status": 203,
            "message": "Deleted Successfully."
        })
        
    except:
        return({
            "status": 404,
            "message": "Promotion Does not Exist"
        })
        
# Number of Seconds in a Day: 86400

# posts of a username
@app.get("/api/userposts/{username}")
def getUserPosts(username: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    try:
        postdb = deta.Base("Milo_Post")
        allPosts = postdb.fetch({"username": username}).items
        allRequiredPosts = []
        keysOfPostsToDelete = []
        for a in allPosts:
            postTime = int(a['timeEpoch'])
            currentTime = int(time.time())
            if currentTime - postTime > 86399:
                #delete post
                keysOfPostsToDelete.append(a['key'])
            else:
                userdb = deta.Base("Milo_User")
                userInstance = userdb.get(username)
                del userInstance['password']
                a['user'] = userInstance
                del a['username']
                allRequiredPosts.append(a)
        for theKey in keysOfPostsToDelete:
            postdb.delete(theKey)
            
            commentdb = deta.Base("Milo_Comment")
            allComments = commentdb.fetch({"postID": theKey}).items
            commentList = []
            for a in allComments:
                    commentList.append(a['key'])
            
            for cl in commentList:
                commentdb.delete(cl)
            
        return allRequiredPosts
    except:
        return({
            "status": 500,
            "message": "Some Error Occurred."
        })

#events of a username
@app.get("/api/userevents/{username}")
def getUserPosts(username: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    try:
        eventdb = deta.Base("Milo_Event")
        allPosts = eventdb.fetch({"username": username}).items
        allRequiredPosts = []
        keysOfPostsToDelete = []
        for a in allPosts:
            postTime = int(a['deadlineTimeEpoch'])
            currentTime = int(time.time())
            if currentTime > postTime:
                #delete post
                keysOfPostsToDelete.append(a['key'])
            else:
                userdb = deta.Base("Milo_User")
                userInstance = userdb.get(username)
                del userInstance['password']
                a['user'] = userInstance
                del a['username']
                allRequiredPosts.append(a)
        for theKey in keysOfPostsToDelete:
            eventdb.delete(theKey)
            
            commentdb = deta.Base("Milo_Event_Comment")
            allComments = commentdb.fetch({"eventID": theKey}).items
            commentList = []
            for a in allComments:
                    commentList.append(a['key'])
            
            for cl in commentList:
                commentdb.delete(cl)
        return allRequiredPosts
    except:
        return({
            "status": 500,
            "message": "Some Error Occurred."
        })

#promotion of a username
@app.get("/api/userpromotions/{username}")
def getUserPromotions(username: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    try:
        eventdb = deta.Base("Milo_Promotion")
        allPosts = eventdb.fetch({"username": username}).items
        allRequiredPosts = []
        for a in allPosts:
            userdb = deta.Base("Milo_User")
            userInstance = userdb.get(username)
            del userInstance['password']
            a['user'] = userInstance
            del a['username']
            allRequiredPosts.append(a)
        return allRequiredPosts
    except:
        return({
            "status": 500,
            "message": "Some Error Occurred."
        })

#particular post
@app.get("/api/post/{key}/{lat}/{long}")
def getUserPromotions(key: str, lat: str, long: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    try:
        # center_point = [{'lat': 18.6329585, 'lng': 73.8058887}]
        # test_point = [{'lat': 18.6143027, 'lng': 73.7991246}]
        
        test_point = [{'lat': float(lat), 'lng': float(long)}]
        
        postdb = deta.Base("Milo_Post")
        thePost = postdb.get(key)
        center_point = thePost['center_point']
        center_point = [{'lat': float(center_point[0]['lat']), 'lng': float(center_point[0]['lng'])}]
        radius = int(thePost['radius'])
        
        if isInside(center_point, test_point, radius):        
            #check epoch
            currentTime = int(time.time())
            postTime = int(thePost['timeEpoch'])
            if currentTime - postTime < 86399:
                # return post
                userdb = deta.Base("Milo_User")
                userInstance = userdb.get(thePost['username'])
                del userInstance['password']
                thePost['user'] = userInstance
                del thePost['username']
                commentsdb = deta.Base("Milo_Comment")
                allComments = commentsdb.fetch({'postID': key}).items
                for theComment in allComments:
                    userdb = deta.Base("Milo_User")
                    userInstance = userdb.get(theComment['username'])
                    del userInstance['password']
                    theComment['user'] = userInstance
                    del theComment['username']
                thePost['comments'] = allComments
                return thePost
            else:
                return {'status': 401, 'message': 'It\'s time is gone' }
        else:
            return {'status': 401, 'message': 'You\'re not in the position to access' }
    except:
        return {'status': 500, 'message': 'Some error occurred'}

#particular event
@app.get("/api/event/{key}/{lat}/{long}")
def getUserPromotions(key: str, lat: str, long: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
        
    try:
        # center_point = [{'lat': 18.6329585, 'lng': 73.8058887}]
        # test_point = [{'lat': 18.6143027, 'lng': 73.7991246}]
        
        test_point = [{'lat': float(lat), 'lng': float(long)}]
        
        eventdb = deta.Base("Milo_Event")
        theEvent = eventdb.get(key)
        center_point = theEvent['center_point']
        center_point = [{'lat': float(center_point[0]['lat']), 'lng': float(center_point[0]['lng'])}]
        radius = int(theEvent['radius'])
        
        if isInside(center_point, test_point, radius):        
            #check epoch
            currentTime = int(time.time())
            eventDeadlineTime = int(theEvent['deadlineTimeEpoch'])
            if currentTime < eventDeadlineTime:
                # return event
                userdb = deta.Base("Milo_User")
                userInstance = userdb.get(theEvent['username'])
                del userInstance['password']
                theEvent['user'] = userInstance
                del theEvent['username']
                commentsdb = deta.Base("Milo_Event_Comment")
                allComments = commentsdb.fetch({'eventID': key}).items
                for theComment in allComments:
                    userdb = deta.Base("Milo_User")
                    userInstance = userdb.get(theComment['username'])
                    del userInstance['password']
                    theComment['user'] = userInstance
                    del theComment['username']
                theEvent['comments'] = allComments
                return theEvent
            else:
                return {'status': 401, 'message': 'It\'s time is gone' }
        else:
            return {'status': 401, 'message': 'You\'re not in the position to access' }
    except:
        return {'status': 500, 'message': 'Some error occurred'}
    
#particular promotion
@app.get("/api/promotion/{key}/{lat}/{long}")
def getUserPromotions(key: str, lat: str, long: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    
    try:
        # center_point = [{'lat': 18.6329585, 'lng': 73.8058887}]
        # test_point = [{'lat': 18.6143027, 'lng': 73.7991246}]
        
        test_point = [{'lat': float(lat), 'lng': float(long)}]
        
        promotiondb = deta.Base("Milo_Promotion")
        thePromotion = promotiondb.get(key)
        center_point = thePromotion['center_point']
        center_point = [{'lat': float(center_point[0]['lat']), 'lng': float(center_point[0]['lng'])}]
        radius = int(thePromotion['radius'])
        
        if isInside(center_point, test_point, radius):        
            # return promotion
            userdb = deta.Base("Milo_User")
            userInstance = userdb.get(thePromotion['username'])
            del userInstance['password']
            thePromotion['user'] = userInstance
            del thePromotion['username']
            commentsdb = deta.Base("Milo_Rating")
            allComments = commentsdb.fetch({'promotionID': key}).items
            for theComment in allComments:
                userdb = deta.Base("Milo_User")
                userInstance = userdb.get(theComment['username'])
                del userInstance['password']
                theComment['user'] = userInstance
                del theComment['username']
            thePromotion['comments'] = allComments
            return thePromotion
        else:
            return {'status': 401, 'message': 'You\'re not in the position to access' }
    except:
        return {'status': 500, 'message': 'Some error occurred'}

# post feed
@app.get("/api/feed/{username}/{lat}/{long}")
def getFeed(username: str, lat: str, long: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    try:
        # center_point = [{'lat': 18.6329585, 'lng': 73.8058887}]
        # test_point = [{'lat': 18.6143027, 'lng': 73.7991246}]
        test_point = [{'lat': float(lat), 'lng': float(long)}]
        postdb = deta.Base("Milo_Post")
        allPosts = postdb.fetch().items
        requiredPosts = []
        for aPost in allPosts:
            if  int(time.time()) - int(aPost['timeEpoch']) < 86399:
                center_point = [{'lat': float(aPost['center_point'][0]['lat']), "lng": float(aPost['center_point'][0]['lng'])}]
                radius = int(aPost['radius'])
                if isInside(center_point, test_point, radius):
                    if username != aPost['username']:
                        userdb = deta.Base("Milo_User")
                        userInstance = userdb.get(aPost['username'])
                        del userInstance['password']
                        aPost['user'] = userInstance
                        del aPost['username']
                        aPost['timeEpoch'] = int(aPost['timeEpoch'])
                        requiredPosts.append(aPost)
        requiredPosts = sorted(requiredPosts, key = lambda i: i['timeEpoch'],reverse=True)
        return requiredPosts
    except:
        return {'status': 500, 'message': 'Some error occurred'}

# hashtags post feed
@app.get("/api/hashfeed/{username}/{lat}/{long}")
def getFeed(username: str, lat: str, long: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    try:
        # center_point = [{'lat': 18.6329585, 'lng': 73.8058887}]
        # test_point = [{'lat': 18.6143027, 'lng': 73.7991246}]
        test_point = [{'lat': float(lat), 'lng': float(long)}]
        postdb = deta.Base("Milo_Post")
        allPosts = postdb.fetch().items
        requiredPosts = []
        for aPost in allPosts:
            if  int(time.time()) - int(aPost['timeEpoch']) < 86399:
                center_point = [{'lat': float(aPost['center_point'][0]['lat']), "lng": float(aPost['center_point'][0]['lng'])}]
                radius = int(aPost['radius'])
                if isInside(center_point, test_point, radius):
                    if username != aPost['username']:
                        userdb = deta.Base("Milo_User")
                        userInstance = userdb.get(aPost['username'])
                        del userInstance['password']
                        aPost['user'] = userInstance
                        del aPost['username']
                        aPost['timeEpoch'] = int(aPost['timeEpoch'])
                        
                        #check username hash and post hash intersection
                        hashdb = deta.Base("Milo_Hashtag")
                        requiredHashList = hashdb.get(username)['value']
                        if common_member(requiredHashList, aPost['hashtags']):
                            requiredPosts.append(aPost)
        requiredPosts = sorted(requiredPosts, key = lambda i: i['timeEpoch'],reverse=True)
        return requiredPosts
    except:
        return {'status': 500, 'message': 'Some error occurred'}
    
# event feed
@app.get("/api/eventfeed/{username}/{lat}/{long}")
def getFeed(username: str, lat: str, long: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    try:
        # center_point = [{'lat': 18.6329585, 'lng': 73.8058887}]
        # test_point = [{'lat': 18.6143027, 'lng': 73.7991246}]
        test_point = [{'lat': float(lat), 'lng': float(long)}]
        postdb = deta.Base("Milo_Event")
        allPosts = postdb.fetch().items
        requiredPosts = []
        for aPost in allPosts:
            if  int(time.time()) < int(aPost['deadlineTimeEpoch']):
                center_point = [{'lat': float(aPost['center_point'][0]['lat']), "lng": float(aPost['center_point'][0]['lng'])}]
                radius = int(aPost['radius'])
                if isInside(center_point, test_point, radius):
                    if username != aPost['username']:
                        userdb = deta.Base("Milo_User")
                        userInstance = userdb.get(aPost['username'])
                        del userInstance['password']
                        aPost['user'] = userInstance
                        del aPost['username']
                        aPost['deadlineTimeEpoch'] = int(aPost['deadlineTimeEpoch'])
                        requiredPosts.append(aPost)
        requiredPosts = sorted(requiredPosts, key = lambda i: i['deadlineTimeEpoch'],reverse=True)
        return requiredPosts
    except:
        return {'status': 500, 'message': 'Some error occurred'}
    
# promotion feed
@app.get("/api/promotionfeed/{username}/{lat}/{long}")
def getFeed(username: str, lat: str, long: str, Authorization: Optional[str] = Header(None)):
    
    if validateToken(Authorization) is False:
        return {
            "status": 401,
            "message": "Invalid Token"
        }
    try:
        # center_point = [{'lat': 18.6329585, 'lng': 73.8058887}]
        # test_point = [{'lat': 18.6143027, 'lng': 73.7991246}]
        test_point = [{'lat': float(lat), 'lng': float(long)}]
        postdb = deta.Base("Milo_Promotion")
        allPosts = postdb.fetch().items
        requiredPosts = []
        for aPost in allPosts:
            center_point = [{'lat': float(aPost['center_point'][0]['lat']), "lng": float(aPost['center_point'][0]['lng'])}]
            radius = int(aPost['radius'])
            if isInside(center_point, test_point, radius):
                if username != aPost['username']:
                    userdb = deta.Base("Milo_User")
                    userInstance = userdb.get(aPost['username'])
                    del userInstance['password']
                    aPost['user'] = userInstance
                    del aPost['username']
                    requiredPosts.append(aPost)
        return requiredPosts
    except:
        return {'status': 500, 'message': 'Some error occurred'}
    
# delete all redundant post event promotion
@app.get("/api/deleteredundantstuff")
def DeleteStuff(Authorization: Optional[str] = Header(None)):
    try:
        #posts
        postdb = deta.Base("Milo_Post")
        allPosts = postdb.fetch().items
        keysOfPostsToDelete = []
        for a in allPosts:
            postTime = int(a['timeEpoch'])
            currentTime = int(time.time())
            if currentTime - postTime > 86399:
                #delete post
                keysOfPostsToDelete.append(a['key'])
        for theKey in keysOfPostsToDelete:
            postdb.delete(theKey)
            commentdb = deta.Base("Milo_Comment")
            allComments = commentdb.fetch({"postID": theKey}).items
            commentList = []
            for a in allComments:
                    commentList.append(a['key'])
            for cl in commentList:
                commentdb.delete(cl)
        #events
        eventdb = deta.Base("Milo_Event")
        allPosts = eventdb.fetch().items
        keysOfPostsToDelete = []
        for a in allPosts:
            postTime = int(a['deadlineTimeEpoch'])
            currentTime = int(time.time())
            if currentTime > postTime:
                #delete post
                keysOfPostsToDelete.append(a['key'])
        for theKey in keysOfPostsToDelete:
            eventdb.delete(theKey)
            commentdb = deta.Base("Milo_Event_Comment")
            allComments = commentdb.fetch({"eventID": theKey}).items
            commentList = []
            for a in allComments:
                    commentList.append(a['key'])
            for cl in commentList:
                commentdb.delete(cl)
        return {'status': 200, 'message': 'Job Done'}
    except:
        return {'status': 500, 'message': 'Some error occurred'}