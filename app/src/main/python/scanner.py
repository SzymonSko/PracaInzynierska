import cv2
import math
import numpy as np
import os
import io
import base64
from os.path import dirname, join
import imutils

def centroid(contour):
    M = cv2.moments(contour)
    cx = int(round(M['m10']/M['m00']))
    cy = int(round(M['m01']/M['m00']))
    centre = (cx, cy)
    return centre

def asign_score(score_boundaries, hole_dist):

    score = 0

    if score_boundaries[0]>hole_dist:
        score = 10
    for i in range(1, len(score_boundaries)):
        if score_boundaries[i-1]<=hole_dist<score_boundaries[i]:
            score = len(score_boundaries) - i
    return score


def process(array):
    global marked, total_score
    array = bytes(array)
    decode = cv2.imdecode(np.frombuffer(array, np.uint8), -1)
    img = decode[:,:-1,:];
    img = cv2.resize(img,(640,640))

    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    h,s,v = cv2.split(hsv)

    v_mask = cv2.inRange(v, 0, 155)

    contours = cv2.findContours(v_mask.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    contours = imutils.grab_contours(contours)

    for c in contours:

        if cv2.contourArea(c) > 10000:
            cv2.drawContours(img, [c], -1, (0, 255, 0), 2)
            area_max = cv2.contourArea(c)

    radius_max = np.sqrt(area_max / np.pi)
    section_size = radius_max / 9

    centre_v_mask = cv2.inRange(v, 215, 255)
    contours = cv2.findContours(centre_v_mask.copy(), cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)
    contours = imutils.grab_contours(contours)

    for c in contours:
        if cv2.contourArea(c) > 10:
            centre_coords = centroid(c)

    h_mask = cv2.inRange(h, 0, 30)
    h_mask = cv2.medianBlur(h_mask, 11)
    contours = cv2.findContours(h_mask.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    contours = imutils.grab_contours(contours)

    holes = []
    hole_dists = []


    score_boundaries = []
    for i in range(1,10): #calculate other rings

        score_boundaries.append(int(i*(section_size)))

    total_score = 0
    for c in contours: #plot bullet holes

        if cv2.contourArea(c) > 1:
            x,y,w,h = cv2.boundingRect(c)
            pts =[(x, y), (x+w, y), (x, y+h), (x+w, y+h)]

            centre_holes = centroid(c)
            pts.append(centre_holes)

            point_score = 0
            for pt in c:
                pt = pt[0] # contour points have an extra pair of brackets [[x,y]]
                X = pt[0]
                Y = pt[1]

                hole_dist = np.sqrt((X-centre_coords[0])**2 + (Y - centre_coords[1])**2)
                hole_dists.append(hole_dist)
                score = asign_score(score_boundaries, hole_dist)

                if score>point_score:
                    point_score = score
            total_score += point_score
            cv2.circle(img, (centre_holes), 1, (0, 0, 255), -1)
            cv2.rectangle(img, (x,y),(x+w,y+h),(0,0,255),2)
            cv2.drawContours(img, [c], -1, (0, 255, 0), 1)

            marked = cv2.putText(img, str(point_score), (centre_holes[0] - 20, centre_holes[1] + 20),
            cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2)

    marked = cv2.imencode('.jpg', marked)

    return marked, total_score

def points():
    return total_score

def check():
    return marked[1].tobytes()

