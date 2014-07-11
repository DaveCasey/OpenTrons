////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

function Button(parent,clickEvent,index){
	this.parent = parent;

	this.div;

	this.index = index;

	this.clickEvent = clickEvent;
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Button.prototype.update = function(){
	if(app.robot.listening) this.div.style.border = 'solid '+app.borderThickness+'px white';
	else this.div.style.border = 'solid '+app.borderThickness+'px '+app.colors.green;
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Button.prototype.setup = function(){
	this.div = document.createElement('div');
	this.div.className = 'abs';
	this.div.style.borderRadius = '50%';
	this.div.style.border = 'solid '+app.borderThickness+'px white';
	this.div.style.overflow = 'hidden';

	if(app.backend.testGUI) this.div.onmousedown = this.clickEvent;
	this.div.ontouchstart = this.clickEvent;

	this.img = document.createElement('img');
	this.img.className = 'abs fill';
	this.img.style.left = '0px';
	this.img.style.top = '0px';

	var filename;
	if(this.parent.axis==='z'){
		if(this.index===0) filename = './img/arrow_down.png';
		else filename = './img/arrow_up.png';
	}
	else{
		if(this.index===0) filename = './img/arrow_left.png';
		else filename = './img/arrow_right.png';
	}

	this.img.src = filename;

	this.div.appendChild(this.img);

	this.parent.div.appendChild(this.div);
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Button.prototype.resize = function(){
	var size = this.parent.buttonSize;

	this.div.style.border = 'solid '+app.borderThickness+'px white';

	this.div.style.width = size+'px';
	this.div.style.height = size+'px';

	var pos = this.parent.pos[this.index];

	var left = pos.x-(size/2);
	var top = pos.y-(size/2);

	this.div.style.left = left+'px';
	this.div.style.top = top+'px';

	this.update();
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Button.prototype.press = function(){
	this.clickEvent();
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Button.prototype.release = function(){
	//
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////