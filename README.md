GWTUVExport
==================
`Apache 2.0 License`
##[Demo](http://akjava.github.io/GWTUVExport/war/GWTUVExport.html)

##what is this?
for three.js json model

There are a way extract uv-layout in Blender.
But I'd like to extract uv-layout fill material color without lines.
I'm going to use the image for create my own texture.

I'm not familiar with Blender and Python,that's why i made this with GWT/java.

![](http://akjava.github.io/GWTUVExport/imgs/filled_uv.png)

Only Tested Chrome.

actually Meshfacematerial can do beter things,however Meshfacematerial is slow.using single texture is fastest.
##Known problems
###Color problem
[Materials from Blender to Three.JS: Colors seem to be different](http://blender.stackexchange.com/questions/34728/materials-from-blender-to-three-js-colors-seem-to-be-different)
###Model size problem
maybe can not handle too big files.
###Edge remaining problem
when texture is not complete filled(remaining some transparent area).some edge seems problem.you should extend area by yourself

default THREE.LinearMipMapLinearFilter is really not good at viewing from far.

use  minFilter = THREE.NearestFilter





