////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

function Line(parent){
	this.parent = parent;

	this.div;
	this.touch;
	this.bar;
	this.indicator;

	this.relPos = 0;

	this.isTouched = false;
	this.sentMessage = false;

	this.pos = {
		'left':undefined,
		'top':undefined,
		'width':undefined,
		'height':undefined
	},
	this.real = {
		'left':undefined,
		'top':undefined,
		'width':undefined,
		'height':undefined
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Line.prototype.update = function(){
	if(app.robot.listening){
		this.div.style.backgroundColor = 'white';
	}
	else this.div.style.backgroundColor = app.colors.blue;

	if(this.isTouched || (!app.robot.listening && this.sentMessage)){
		this.bar.style.display = 'block';
	}
	else{
		this.bar.style.display = 'none';
		this.sentMessage = false;
	}

	if(this.parent.axis==='z'){
		var yPos = (app.robot.z*this.pos.height)+(this.pos.top)-app.gutter/2;
		this.indicator.style.top = yPos+'px';
	}
	else{
		var xPos = (app.robot[this.parent.axis]*this.pos.width)+(this.pos.left);
		this.indicator.style.left = xPos+'px';
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Line.prototype.setup = function(){
	var self = this;

	this.div = document.createElement('div');
	this.div.className = 'abs';
	this.div.style.backgroundColor = 'white';

	this.touch = document.createElement('div');
	this.touch.className = 'abs';

	function axis_touch(event){
		event.preventDefault();
		if(app.robot.listening){
			self.isTouched = true;
			self.bar.style.display = 'block';
			axis_move(event);
		}
	}

	function axis_move(event){
		event.preventDefault();

		var touch = event.changedTouches ? event.changedTouches[0] : event;

		if(self.isTouched && touch){

			var relX = (touch.pageX-(self.real.left+app.left+app.borderThickness))/self.pos.width;
			var relY = (touch.pageY-(self.real.top+app.top+app.borderThickness))/self.pos.height;

			if(relX<0||relX>1 || relY<0||relY>1) axis_moveout(event);
			else{
				if(self.parent.axis==='z'){
					self.bar.style.top = Math.floor(relY*self.pos.height)+self.pos.top-app.gutter/2+'px';
					self.relPos = relY;
				}
				else{
					self.bar.style.left = Math.floor(relX*self.pos.width)+self.pos.left+'px';
					self.relPos = relX;
				}
			}
		}
	}

	function axis_release(event){
		event.preventDefault();
		if(self.isTouched){
			self.isTouched = false;

			// send to openTrons
			var tag = self.parent.axis;
			var targetMsg = {};
			targetMsg[tag] = self.relPos;

			self.sentMessage = true;

			try{
				app.backend.jog(targetMsg);
			}
			catch(error){
				console.log('ERROR sending to openTrons');
				console.log(error);
			}
		}
	}

	function axis_moveout(event){
		event.preventDefault();
		if(self.isTouched){
			self.isTouched = false;
			self.bar.style.display = 'none';
		}
	}

	if(app.mouse){
		self.touch.addEventListener('mousedown', axis_touch,false);
		self.touch.addEventListener('mouseup',axis_release,false);
		self.touch.addEventListener('mouseout',axis_moveout,false);
		self.touch.addEventListener('mousemove',axis_move,false);
	}
	else{
		self.touch.addEventListener('touchstart', axis_touch,false);
		self.touch.addEventListener('touchend',axis_release,false);
		self.touch.addEventListener('touchleave',axis_moveout,false);
		self.touch.addEventListener('touchmove',axis_move,false);
	}

	this.bar = document.createElement('div');
	this.bar.style.backgroundColor = 'white';
	this.bar.style.display = 'none';
	this.bar.className = 'abs';

	this.indicator = document.createElement('div');
	this.indicator.style.backgroundColor = app.colors.blue;
	//this.indicator.style.borderRadius = '50%';
	this.indicator.className = 'abs';

	this.parent.div.appendChild(this.div);
	this.parent.div.appendChild(this.bar);
	this.parent.div.appendChild(this.indicator);
	this.parent.div.appendChild(this.touch);
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Line.prototype.resize = function(){

	if(this.parent.axis==='z'){
		var left = this.parent.parent.width-this.parent.parent.buttonWidth;
		var top = this.parent.parent.buttonHeight+app.gutter;
		var width = this.parent.parent.buttonWidth;
		var height = (this.parent.parent.height-this.parent.parent.buttonHeight-app.gutter)-top;
	}
	else{
		if(this.parent.axis==='a'){
			var left = 0;
		}
		else{
			var left = this.parent.parent.buttonWidth+app.gutter;
		}
		var top = this.parent.parent.height-(this.parent.parent.buttonHeight*3+app.gutter*2);
		var width = this.parent.parent.buttonWidth*2+app.gutter;
		var height = app.borderThickness;
	}

	if(this.parent.axis==='z'){
		this.div.style.left = left+(width/2-app.borderThickness/2) + 'px';
		this.div.style.top = top + 'px';
		this.div.style.width = app.borderThickness + 'px';
		this.div.style.height = height + 'px';
	}
	else{
		this.div.style.left = left + 'px';
		this.div.style.top = (top+(this.parent.parent.buttonHeight/2)-(app.borderThickness/2)) + 'px';
		this.div.style.width = width + 'px';
		this.div.style.height = height + 'px';
	}

	if(this.parent.axis==='z'){
		this.pos.left = left;
		this.pos.top = top;
		this.pos.width = this.parent.parent.buttonWidth;
		this.pos.height = height;
	}
	else{
		this.pos.left = left;
		this.pos.top = top;
		this.pos.width = width;
		this.pos.height = this.parent.parent.buttonHeight;
	}

	this.touch.style.left = this.pos.left + 'px';
	this.touch.style.top = this.pos.top + 'px';
	this.touch.style.width = this.pos.width + 'px';
	this.touch.style.height = this.pos.height + 'px';

	if(this.parent.axis==='z'){
		this.bar.style.width = this.pos.width+'px';
		this.bar.style.height = app.gutter+'px';
		this.bar.style.left = this.pos.left+'px';

		this.indicator.style.width = this.pos.width+'px';
		this.indicator.style.height = app.gutter+'px';

		this.indicator.style.left = this.pos.left+'px';
	}
	else{
		this.bar.style.height = this.pos.height+'px';
		this.bar.style.width = app.borderThickness+'px';
		this.bar.style.top = this.pos.top+'px';

		this.indicator.style.height = this.pos.height+'px';
		this.indicator.style.width = app.borderThickness+'px';

		this.indicator.style.top = this.pos.top+'px';
	}

	this.real.left = this.pos.left + this.parent.parent.left;
	this.real.top = this.pos.top + this.parent.parent.top;

	this.update();
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////