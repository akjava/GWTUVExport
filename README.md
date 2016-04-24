GWTUVExport
==================
`Apache 2.0 License`

##what is this?
for three.js json model

There are a way extract uv-layout in Blender.
But I'd like to extract uv-layout fill material color without lines.
I'm going to use the image for create my own texture.

I'm not familiar with Blender and Python,that's why i made this with GWT/java.

![](http://akjava.github.io/GWTUVExport/imgs/filled_uv.png)

Only Tested Chrome.
##Known problems
###Color problem
[Materials from Blender to Three.JS: Colors seem to be different](http://blender.stackexchange.com/questions/34728/materials-from-blender-to-three-js-colors-seem-to-be-different)
###Model size problem
maybe can not handle too big files.
###Edge remaining problem
this is not complete filled.some edge seems problem.you should extend area by yourself

##[Demo](http://akjava.github.io/GWTUVExport/war/GWTUVExport.html)

