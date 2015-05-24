/**
 * Created by wernermostert on 2015/05/16.
 */
var gl;

function initGL(canvas) {
    try {
        gl = canvas.getContext("experimental-webgl");
        gl.viewportWidth = canvas.width;
        gl.viewportHeight = canvas.height;
    } catch (e) {
    }
    if (!gl) {
        alert("Could not initialise WebGL, sorry :-(");
    }
}

function webGLStart() {
    var canvas = document.getElementById("canvas");
    initGL(canvas);
    initShaders();
    initTexture();
    initObjects();

    gl.clearColor(0.0, 0.0, 0.0, 1.0);
    gl.enable(gl.DEPTH_TEST);

    document.onkeydown = handleKeyDown;
    document.onkeyup = handleKeyUp;
    document.onmousemove = handleMouseMove;
    canvas.onmousedown = handleMouseDown;
    document.onmouseup = handleMouseUp;

    tick();
}


var RGBa = [0.7,0.7,0.7];
var pointLocation = [2.0,.0,0.0];
var pointRGB = [0.7,0.7,0.7];


function drawScene() {
    gl.viewport(0, 0, gl.viewportWidth, gl.viewportHeight);
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);


    mat4.perspective(45, gl.viewportWidth / gl.viewportHeight, 0.1, 500.0, pMatrix);
    mat4.identity(mvMatrix);

    gl.uniform1i(shaderProgram.v_useParticles, false);
    gl.uniform1i(shaderProgram.f_useParticles, false);

    mat4.rotate(mvMatrix, degToRad(-pitch), [1, 0, 0]);
    mat4.rotate(mvMatrix, degToRad(-yaw), [0, 1, 0]);
    mat4.translate(mvMatrix, [-xPos, -yPos, -zPos]);
    mat4.translate(mvMatrix,[0,-2,-8]);
    //if(parts > 5){
    //    RGBa = [0.7,0.7,1.0];
    //}else{
    //    RGBa = [0.7,0.7,0.7];
    //}


    //Set-up lighting
    gl.uniform3f(
        shaderProgram.ambientColorUniform,
        parseFloat(RGBa[0]),
        parseFloat(RGBa[1]),
        parseFloat(RGBa[2])
    );

    gl.uniform3f(
        shaderProgram.pointLightingLocationUniform,
        parseFloat(pointLocation[0]),
        parseFloat(pointLocation[1]),
        parseFloat(pointLocation[2])
    );

    gl.uniform3f(
        shaderProgram.pointLightingColorUniform,
        parseFloat(pointRGB[0]),
        parseFloat(pointRGB[1]),
        parseFloat(pointRGB[2])
    );

    world.primitives.cube.draw(function(){
        mat4.scale(mvMatrix,[1.0,0.1,1.0]);
    },woodTexture);

    world.primitives.cube.draw(function(){
        mat4.translate(mvMatrix,[-0.8,2.0,0.0]);
        mat4.scale(mvMatrix,[0.1,2.0,0.1]);
    },woodTexture);

    world.primitives.cube.draw(function(){
        mat4.translate(mvMatrix,[0,3.8,0.0]);
        mat4.scale(mvMatrix,[1.0,0.1,0.1]);
    },woodTexture);


    //head
    if(parts > 0) {
        world.primitives.sphere.draw(function () {
            mat4.translate(mvMatrix, [0.4, 3.3, 0.0]);
            mat4.scale(mvMatrix, [0.3, 0.3, 0.3]);

        });
    }
    if(parts > 1){
        //torso
        world.primitives.cube.draw(function(){
            mat4.translate(mvMatrix,[0.4,2.2,0.0]);
            mat4.scale(mvMatrix,[0.35,0.6,0.12]);
        });
        //neck
        world.primitives.cube.draw(function(){
            mat4.translate(mvMatrix,[0.4,3.0,0.0]);
            mat4.scale(mvMatrix,[0.2,0.7,0.2]);
            mat4.scale(mvMatrix,[0.5,0.5,0.5]);
        });
    }

    if(parts > 2){
        //right arm
        world.primitives.cube.draw(function(){
            mat4.translate(mvMatrix,[1.0,2.4,0.0]);
            mat4.rotate(mvMatrix,degToRad(45),[0,0,1]);

            mat4.scale(mvMatrix,[0.1,0.45,0.1]);

        });
    }

    if(parts > 3){
        //left arm
        world.primitives.cube.draw(function(){
            mat4.translate(mvMatrix,[-0.2,2.4,0.0]);
            mat4.rotate(mvMatrix,degToRad(-45),[0,0,1]);
            mat4.scale(mvMatrix,[0.1,0.45,0.1]);

        });
    }

    if(parts > 4){
        //left leg
        world.primitives.cube.draw(function(){
            mat4.translate(mvMatrix,[-0.1,1.2,0.0]);
            mat4.rotate(mvMatrix,degToRad(-25),[0,0,1]);
            mat4.scale(mvMatrix,[0.1,0.6,0.1]);

        });
    }

    if(parts > 5){
        //right leg
        world.primitives.cube.draw(function(){
            mat4.translate(mvMatrix,[0.9,1.2,0.0]);
            mat4.rotate(mvMatrix,degToRad(25),[0,0,1]);
            mat4.scale(mvMatrix,[0.1,0.6,0.1]);

        });
    }
}

//Camera
var lastTime = 0;
var joggingAngle = 0;
var xPos;
var zPos;
function animate() {
    var timeNow = new Date().getTime();

    if (lastTime != 0) {
        var elapsed = timeNow - lastTime;
        if (speedFB != 0) {
            xPos -= Math.sin(degToRad(yaw)) * speedFB * elapsed;
            zPos -= Math.cos(degToRad(yaw)) * speedFB * elapsed;

            joggingAngle += elapsed * 0.6; // 0.6 "fiddle factor" - makes it feel more realistic :-)
            yPos = Math.sin(degToRad(joggingAngle)) / 20 + 0.4
        }

        if(speedLR != 0){
            xPos -= Math.sin(degToRad(yaw + 90)) * speedLR * elapsed;
            zPos -= Math.cos(degToRad(yaw + 90)) * speedLR * elapsed;
        }

        yaw += yawRate * elapsed;
        pitch += pitchRate * elapsed;

    }

    lastTime = timeNow;
}


function tick() {
    requestAnimFrame(tick);
    handleKeys();
    drawScene();
    animate();
}
