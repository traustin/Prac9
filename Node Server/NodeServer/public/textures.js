/**
 * Created by wernermostert on 2015/05/16.
 */
function handleLoadedTexture(texture) {
    gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);
    gl.bindTexture(gl.TEXTURE_2D, texture);
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, texture.image);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR_MIPMAP_NEAREST);
    gl.generateMipmap(gl.TEXTURE_2D);

    gl.bindTexture(gl.TEXTURE_2D, null);


}

var skinTexture;
var woodTexture;
var redTexture;
var eyeTexture;
var torsoTexture;
var faceTexture;


function initTexture() {
    skinTexture = gl.createTexture();
    skinTexture.image = new Image();
    skinTexture.image.onload = function () {
        handleLoadedTexture(skinTexture)
    };

    skinTexture.image.src = "./img/skin.jpg";

    woodTexture = gl.createTexture();
    woodTexture.image = new Image();
    woodTexture.image.onload = function () {
        handleLoadedTexture(woodTexture)
    };

    woodTexture.image.src = "./img/wood.jpg";

    redTexture = gl.createTexture();
    redTexture.image = new Image();
    redTexture.image.onload = function () {
        handleLoadedTexture(redTexture)
    };

    redTexture.image.src = "./img/red.jpg";

    eyeTexture = gl.createTexture();
    eyeTexture.image = new Image();
    eyeTexture.image.onload = function () {
        handleLoadedTexture(eyeTexture)
    };

    eyeTexture.image.src = "./img/eye.jpg";

    torsoTexture = gl.createTexture();
    torsoTexture.image = new Image();
    torsoTexture.image.onload = function () {
        handleLoadedTexture(torsoTexture)
    };

    torsoTexture.image.src = "./img/torso.jpg";

    faceTexture = gl.createTexture();
    faceTexture.image = new Image();
    faceTexture.image.onload = function () {
        handleLoadedTexture(faceTexture)
    };

    faceTexture.image.src = "./img/face.jpg";
    
}