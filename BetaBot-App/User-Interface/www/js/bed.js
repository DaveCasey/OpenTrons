////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

function Bed(parent){
	this.parent = parent;

	this.canvas = undefined;

	this.touchScreen = undefined;

	this.blobContainer = undefined;

	this.vertBot = undefined;
	this.horizBot = undefined;

	this.vertFinger = undefined;
	this.horizFinger = undefined;
	
	this.width = undefined;
	this.height = undefined;
	this.left = undefined;
	this.top = undefined;

	this.touched = false;
	this.touchX = 0;
	this.touchY = 0;
	this.paddingX = 0;
	this.paddingY = 0;

	this.buttons = {
		'left' : undefined,
		'right' : undefined,
		'up' : undefined,
		'down' : undefined
	};

	this.setup();

	this.blobs = {};
};

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Bed.prototype.touchpadEvent = function(touchX,touchY){
	if(app.backend.listening===1){
		this.touchX = touchX;
		this.touchY = touchY;
		this.draw();
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Bed.prototype.touchpadRelease = function(){
	if(app.backend.listening===1){
		var targetMsg = {
			'x':this.touchX,
			'y':this.touchY
		}
		try{
			app.backend.jog(targetMsg);
		}
		catch(error){
			console.log('ERROR sending to openTrons');
			console.log(error);
		}
	}
	this.draw();
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Bed.prototype.draw = function(){
	var self = this;
	requestAnimFrame(function(){

		for(var n in self.blobs){
			self.blobs[n].updated = false;
		}

		for(var n in app.ingredients){
			var i = app.ingredients[n];
			if(i.x!=undefined){
				if(!self.blobs[n]){
					var newDiv = document.createElement('div');
					newDiv.className = 'abs ball';
					newDiv.style.backgroundColor = app.colors.green;
					self.blobContainer.appendChild(newDiv);
					var newBlob = {
						'updated' : false,
						'x' : undefined,
						'y' : undefined,
						'div' : newDiv
					};
					self.blobs[n] = newBlob;
				}
				self.blobs[n].updated = true;
				self.blobs[n].x = i.x;
				self.blobs[n].y = i.y;
				if(n==app.pages.jog.mixer.currentIngredient){
					self.blobs[n].div.style.backgroundColor = app.colors.green;
				}
				else{
					self.blobs[n].div.style.backgroundColor = app.colors.blue;
				}
			}
		}

		var drawWidth = self.width*(1-(self.paddingX*2));
		var drawHeight = self.height*(1-(self.paddingY*2));

		var lineWidth = app.borderThickness;

		for(var n in self.blobs){
			if(!self.blobs[n].updated){
				self.blobs[n].div.parentNode.removeChild(self.blobs[n].div);
				delete self.blobs[n];
			}
			else{
				self.blobs[n].div.style.width = app.gutter+'px';
				self.blobs[n].div.style.height = app.gutter+'px';
				self.blobs[n].div.style.left = Math.floor(self.blobs[n].x*drawWidth)-(app.gutter/2)+'px';
				self.blobs[n].div.style.top = Math.floor(self.blobs[n].y*drawHeight)-(app.gutter/2)+'px';
			}
		}

		if(app.backend.listening) {
			self.drawCrosshairs = false;
			self.canvas.style.border = 'solid '+app.borderThickness+'px white';
		}
		else{
			self.canvas.style.border = 'solid '+app.borderThickness+'px '+app.colors.blue;
		}

		if(self.touched){
			var displayX = (self.touchX*drawWidth)+(self.width*self.paddingX);
			var displayY = (self.touchY*drawHeight)+(self.height*self.paddingY);

			self.vertFinger.style.display = 'block';
			self.horizFinger.style.display = 'block';

			self.vertFinger.style.left = (displayX-app.borderThickness/2)+'px';
			self.horizFinger.style.top = (displayY-app.borderThickness/2)+'px';
		}
		else{
			self.vertFinger.style.display = 'none';
			self.horizFinger.style.display = 'none';
		}

		var backendX = (app.backend.pos.x*drawWidth)+(self.width*self.paddingX);
		var backendY = (app.backend.pos.y*drawHeight)+(self.height*self.paddingY);

		self.vertBot.style.left = (backendX-app.borderThickness/2)+'px';
		self.horizBot.style.top = (backendY-app.borderThickness/2)+'px';
	});
};

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Bed.prototype.setup = function(){

	var self = this;

	var jog_container = document.getElementById('jog_container');
	jog_container.style.backgroundColor = app.colors.grey;

	self.canvas = document.getElementById('bed_canvas');

	this.blobContainer = document.createElement('div');
	this.blobContainer.className = 'abs fill';

	this.canvas.appendChild(this.blobContainer);

	this.vertBot = document.createElement('div');
	this.vertBot.className = 'abs';
	this.vertBot.style.backgroundColor = app.colors.blue;
	this.vertBot.style.top = '0px';

	this.canvas.appendChild(this.vertBot);

	this.horizBot = document.createElement('div');
	this.horizBot.className = 'abs';
	this.horizBot.style.backgroundColor = app.colors.blue;
	this.horizBot.style.left = '0px';

	this.canvas.appendChild(this.horizBot);

	this.vertFinger = document.createElement('div');
	this.vertFinger.className = 'abs';
	this.vertFinger.style.backgroundColor = 'white';
	this.vertFinger.style.top = '0px';

	this.canvas.appendChild(this.vertFinger);

	this.horizFinger = document.createElement('div');
	this.horizFinger.className = 'abs';
	this.horizFinger.style.backgroundColor = 'white';
	this.horizFinger.style.left = '0px';

	this.canvas.appendChild(this.horizFinger);

	this.touchScreen = document.createElement('div');
	this.touchScreen.className = 'abs fill';

	this.canvas.appendChild(this.touchScreen);

	function bed_touchstart(event){
		event.preventDefault();
		if(!self.touched){
			self.touched = true;
			bed_touchmove(event);
		}
	}

	function bed_touchend(event){
		event.preventDefault();
		if(self.touched){
			self.touchpadRelease();
			self.touched = false;
			self.drawCrosshairs = true;
		}
	}

	function bed_touchmove(event){
		event.preventDefault();

		var touch = event.changedTouches ? event.changedTouches[0] : event;

		if(self.touched && touch){

			var relX = (((touch.pageX-(self.left+app.left+app.borderThickness))/self.width)*2)-1;
			var relY = (((touch.pageY-(self.top+app.top+app.borderThickness))/self.height)*2)-1;

			relX  = ((relX/(1-self.paddingX*2))+1)/2;
			relY  = ((relY/(1-self.paddingY*2))+1)/2;

			if(relX<0||relX>1 || relY<0||relY>1) bed_touchout(event);
			else self.touchpadEvent(relX,relY);
		}
	}

	function bed_touchout(event){
		self.touched=false;
		self.draw();
	}

	if(app.mouse){
		self.touchScreen.addEventListener('mousedown', bed_touchstart,false);
		self.touchScreen.addEventListener('mouseup',bed_touchend,false);
		self.touchScreen.addEventListener('mouseout',bed_touchout,false);
		self.touchScreen.addEventListener('mousemove',bed_touchmove,false);
	}
	else{
		self.touchScreen.addEventListener('touchstart', bed_touchstart,false);
		self.touchScreen.addEventListener('touchend',bed_touchend,false);
		self.touchScreen.addEventListener('touchleave',bed_touchout,false);
		self.touchScreen.addEventListener('touchmove',bed_touchmove,false);
	}

	for(var n in this.buttons){
		this.buttons[n] = document.getElementById('bed_'+n);
	}

	function xDown(){
		app.backend.buttonStep('x',-app.stepAmount);
	}
	function xUp(){
		app.backend.buttonStep('x',app.stepAmount);
	}
	function yDown(){
		app.backend.buttonStep('y',-app.stepAmount);
	}
	function yUp(){
		app.backend.buttonStep('y',app.stepAmount);
	}

	if(app.mouse){
		self.buttons.left.addEventListener('mousedown', xDown ,false);
		self.buttons.right.addEventListener('mousedown', xUp ,false);
		self.buttons.up.addEventListener('mousedown', yDown ,false);
		self.buttons.down.addEventListener('mousedown', yUp ,false);
	}
	else{
		self.buttons.left.addEventListener('touchstart', xDown ,false);
		self.buttons.right.addEventListener('touchstart', xUp ,false);
		self.buttons.up.addEventListener('touchstart', yDown ,false);
		self.buttons.down.addEventListener('touchstart', yUp ,false);
	}
};

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Bed.prototype.resize = function(){

	var bed_div = document.getElementById('bed_div');

	bed_div.style.width = this.width+'px';
	bed_div.style.height = this.height+'px';
	bed_div.style.left = this.left+'px';
	bed_div.style.top = this.top+'px';

	this.canvas.style.width = this.width+'px';
	this.canvas.style.height = this.height+'px';

	this.vertBot.style.height = this.height+'px';
	this.vertBot.style.width = app.borderThickness+'px';
	this.horizBot.style.height = app.borderThickness+'px';
	this.horizBot.style.width = this.width+'px';

	this.vertFinger.style.height = this.height+'px';
	this.vertFinger.style.width = app.borderThickness+'px';
	this.horizFinger.style.height = app.borderThickness+'px';
	this.horizFinger.style.width = this.width+'px';

	var buttonDiv = document.getElementById('bed_buttons');
	//buttonDiv.style.backgroundColor = app.colors.grey;
	buttonDiv.style.left = app.borderThickness+'px';
	buttonDiv.style.top = app.borderThickness+'px';

	var buttonSize = this.width/4;

	var leftX = (this.width*.2)-(buttonSize/2);
	var middleX = (this.width*.5)-(buttonSize/2);
	var rightX = (this.width*.8)-(buttonSize/2);

	var topY = (this.height*.25)-(buttonSize/2);
	var middleY = (this.height*.5)-(buttonSize/2);
	var bottomY = (this.height*.75)-(buttonSize/2);

	for(var n in this.buttons){
		//this.buttons[n].style.border = 'solid '+app.borderThickness+'px white';
		this.buttons[n].style.width = buttonSize-(app.borderThickness*2)+'px';
		this.buttons[n].style.height = buttonSize-(app.borderThickness*2)+'px';
		if(n==='up'){
			this.buttons[n].style.left = middleX+'px';
			this.buttons[n].style.top = topY+'px';
		}
		else if(n==='down'){
			this.buttons[n].style.left = middleX+'px';
			this.buttons[n].style.top = bottomY+'px';
		}
		else if(n==='left'){
			this.buttons[n].style.left = leftX+'px';
			this.buttons[n].style.top = middleY+'px';
		}
		else if(n==='right') {
			this.buttons[n].style.left = rightX+'px';
			this.buttons[n].style.top = middleY+'px';
		}
	}

	this.draw();
};

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////