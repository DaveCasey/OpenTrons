////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

function zSlider(parent,axis){
	this.parent = parent;

	this.div = undefined;

	this.pos = [{'x':undefined,'y':undefined},{'x':undefined,'y':undefined}];

	this.axis = axis; // either z, a, b, or c

	this.line;
	this.buttons = {};

	this.gridSize;
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

zSlider.prototype.update = function(){
	this.line.update();
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

zSlider.prototype.setup = function(){

	this.div = document.createElement('div');
	this.div.className = 'abs';

	this.div.style.backgroundColor = 'blue';

	this.parent.div.appendChild(this.div);

	this.line = new Line(this);
	this.line.setup();

	var self = this;
	var buttonStepSize = 0.1;

	function zUp(){
		app.backend.buttonStep('z',-app.stepAmount);
	}

	function zDown(){
		app.backend.buttonStep('z',app.stepAmount);
	}

	this.buttons.up = document.createElement('div');
	this.buttons.up.className = 'abs';

	if(app.mouse) self.buttons.up.addEventListener('mousedown', zUp ,false);
	else self.buttons.up.addEventListener('touchstart', zUp ,false);

	var arrowUp = document.createElement('img');
	arrowUp.src = './img/arrow_up.png';
	arrowUp.style.width = '100%';
	arrowUp.style.marginTop = '-15%';

	this.buttons.up.appendChild(arrowUp);
	this.div.appendChild(this.buttons.up);

	this.buttons.down = document.createElement('div');
	this.buttons.down.className = 'abs';

	if(app.mouse) self.buttons.down.addEventListener('mousedown', zDown ,false);
	else self.buttons.down.addEventListener('touchstart', zDown ,false);

	var arrowDown = document.createElement('img');
	arrowDown.src = './img/arrow_down.png';
	arrowDown.style.width = '100%';
	arrowDown.style.marginTop = '-22%';
	
	this.buttons.down.appendChild(arrowDown);
	this.div.appendChild(this.buttons.down);
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

zSlider.prototype.resize = function(){

	this.gridSize = this.parent.gridSize;

	this.buttons.up.style.width = this.parent.buttonWidth+'px';
	this.buttons.up.style.height = this.parent.buttonHeight+'px';
	this.buttons.up.style.left = this.parent.width-this.parent.buttonWidth+'px';
	this.buttons.up.style.top = '0px';

	this.buttons.down.style.width = this.parent.buttonWidth+'px';
	this.buttons.down.style.height = this.parent.buttonHeight+'px';
	this.buttons.down.style.left = this.parent.width-this.parent.buttonWidth+'px';
	this.buttons.down.style.top = this.parent.height-this.parent.buttonHeight+'px';

	var tempX = this.parent.width-app.gutter*2;

	for(var i=0;i<2;i++){
		var tempY;
		if(i===1) tempY = this.gridSize/2;
		else tempY = this.parent.height-(this.gridSize/2+(app.gutter/2))+app.borderThickness/2;
		this.pos[i] = {'x':tempX,'y':tempY};
	}

	this.line.resize();
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

zSlider.prototype.step = function(direction){
	var targetMsg = {};
	if(this.axis==='z') direction *= -1;
	targetMsg[this.axis] = app.backend.pos[this.axis]+(direction*0.1);

	try{
		app.backend.jog(targetMsg);
	}
	catch(error){
		console.log('ERROR sending to openTrons');
		console.log(error);
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////