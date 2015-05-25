/**
 * Created by wernermostert on 2015/05/16.
 */
var mvMatrix = mat4.create();
var mvMatrixStack = [];
var pMatrix = mat4.create();

function mvPushMatrix() {
    var copy = mat4.create();
    mat4.set(mvMatrix, copy);
    mvMatrixStack.push(copy);
}

function mvPopMatrix() {
    if (mvMatrixStack.length == 0) {
        throw "Invalid popMatrix!";
    }
    mvMatrix = mvMatrixStack.pop();
}

function degToRad(degrees) {
    return degrees * Math.PI / 180;
}

var currentlyPressedKeys = {};

function handleKeyDown(event) {
    currentlyPressedKeys[event.keyCode] = true;


}


function handleKeyUp(event) {
    currentlyPressedKeys[event.keyCode] = false;
    //Enter key press
    if(event.keyCode == 13 && !trainMove){
        trainMove = true;
    }
    if(event.keyCode == 32){
        if(seeThrough) seeThrough = false;
        else seeThrough = true;
    }
}


var pitch = 0;
var pitchRate = 0;

var yaw = 0;
var yawRate = 0;

var xPos = 0;
var yPos = 0.4;
var zPos = 0;

var speedFB = 0;
var speedLR = 0;


function handleKeys(){
    if (currentlyPressedKeys[37] || currentlyPressedKeys[65]) {
        // Left cursor key or A
         speedLR = 0.003;
    } else if (currentlyPressedKeys[39] || currentlyPressedKeys[68]) {
        // Right cursor key or D
         speedLR = -0.003;
    } else {
         speedLR = 0;
    }

    if (currentlyPressedKeys[38] || currentlyPressedKeys[87]) {
        // Up cursor key or W
        speedFB = 0.003;
    } else if (currentlyPressedKeys[40] || currentlyPressedKeys[83]) {
        // Down cursor key
        speedFB = -0.003;
    } else {
        speedFB = 0;
    }

}

var mouseDown = false;
var lastMouseX = null;
var lastMouseY = null;

var sceneRotationMatrix = mat4.create();
mat4.identity(sceneRotationMatrix);

function handleMouseDown(event) {
    mouseDown = true;
    lastMouseX = event.clientX;
    lastMouseY = event.clientY;
}


function handleMouseUp(event) {
    mouseDown = false;
}


function handleMouseMove(event) {
    if (!mouseDown) {
        return;
    }
    var newX = event.clientX;
    var newY = event.clientY;
    var deltaX = newX - lastMouseX;
    yaw -= deltaX / 10;
    var deltaY = newY - lastMouseY;
    pitch -= deltaY / 10;
    lastMouseX = newX;
    lastMouseY = newY;
}