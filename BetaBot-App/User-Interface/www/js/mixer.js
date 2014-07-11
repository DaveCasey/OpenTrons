////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

function Mixer(parent){
	this.parent = parent;

	this.div = undefined;

	this.left;
	this.top;
	this.width;
	this.height;

	this.sliders = {
		'z' : undefined
	};

	this.commands = {
		'save' : undefined,
		'stop' : undefined
	}

	this.pipetteButton = {
		'aspirate' : undefined,
		'blowout' : undefined,
		'droptip' : undefined
	}

	this.ingredientDiv = undefined;
	this.currentIngredient = undefined;
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Mixer.prototype.setup = function(){

	var self = this;

	this.div = document.createElement('div');
	this.div.className = 'abs';

	var zPos = [{'x':2,'y':2},{'x':2,'y':0}];
	this.sliders.z = new zSlider(this,'z',zPos);

	for(var n in this.sliders){
		this.sliders[n].setup();
	}

	for(var n in this.pipetteButton){
		this.pipetteButton[n] = document.createElement('dif');
		this.pipetteButton[n].className = 'abs command';
		this.pipetteButton[n].innerHTML = app.arrows[n];
		this.pipetteButton[n].style.backgroundColor = app.colors.lightGrey;
		this.pipetteButton[n].style.color = app.colors.blue;

		var onClick = (function(){
			var val = undefined;
			if(n==='aspirate'){
				val = 0;
			}
			else if(n==='blowout'){
				val = 0.72727272727; // 16 divided by 22 ( blowout / droptip )
			}
			else if(n==='droptip'){
				val = 1;
			}
			return function(e){
				e.preventDefault();
				var msg = {
					'a' : val
				};
				console.log(JSON.stringify(msg));
				app.backend.jog(msg);
			};
		})();

		this.pipetteButton[n].addEventListener('click',onClick,false);
		this.div.appendChild(this.pipetteButton[n]);
	}

	this.commands.save = document.createElement('div');
	this.commands.save.className = 'abs command';
	this.commands.save.innerHTML = 'SAVE-XYZ';
	this.commands.save.style.color = 'white';
	this.commands.save.style.backgroundColor = app.colors.green;

	this.commands.save.addEventListener('click', function(e){
		e.preventDefault();
		self.save();
	},false);

	this.div.appendChild(this.commands.save);

	this.commands.stop = document.createElement('div');
	this.commands.stop.className = 'abs command';
	this.commands.stop.innerHTML = 'Stop';
	this.commands.stop.style.color = 'white';
	this.commands.stop.style.backgroundColor = app.colors.lightGrey;

	var onStop = function(e){
		e.preventDefault();
		app.backend.stop();
	}

	if(app.mouse)this.commands.stop.addEventListener('mousedown',onStop,false);
	else this.commands.stop.addEventListener('touchstart',onStop,false);

	this.div.appendChild(this.commands.stop);

	this.commands.home = document.createElement('div');
	this.commands.home.className = 'abs command';
	this.commands.home.innerHTML = 'Reset';
	this.commands.home.style.color = 'white';
	this.commands.home.style.backgroundColor = app.colors.lightGrey;

	var onHome = function(e){
		e.preventDefault();
		app.backend.home();
	}

	this.commands.home.addEventListener('click',onHome,false);

	this.div.appendChild(this.commands.home);

	this.ingredientDiv = document.createElement('div');
	this.ingredientDiv.className = 'abs command';
	this.ingredientDiv.style.color = app.colors.green;

	this.div.appendChild(this.ingredientDiv);

	this.parent.div.appendChild(this.div);
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Mixer.prototype.update = function(){
	for(var n in this.sliders){
		this.sliders[n].update();
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Mixer.prototype.updateSliderPositions = function(){
	for(var n in this.sliders){
		this.sliders[n].resize();
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Mixer.prototype.setIngredient = function(ing){
	if(ing){
		this.currentIngredient = ing;
		this.ingredientDiv.innerHTML = app.ingredients[ing].name;
		this.commands.save.style.display = 'block';
	}
	else{
		this.currentIngredient = undefined;
		this.ingredientDiv.innerHTML = '';
		this.commands.save.style.display = 'none';
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Mixer.prototype.save = function(){
	if(this.currentIngredient){
		var ing = app.ingredients[this.currentIngredient];
		if(ing){
			ing.x = app.backend.pos.x;
			ing.y = app.backend.pos.y;
			ing.z = app.backend.pos.z;
			ing.div.getElementsByClassName('jogImage')[0].parentNode.style.borderColor = app.colors.grey;
			this.parent.bed.draw();
		}
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Mixer.prototype.resize = function(){

	this.div.style.left = this.left+'px';
	this.div.style.top = this.top+'px';
	this.div.style.width = this.width+'px';
	this.div.style.height = this.height+'px';

	this.commands.save.style.top = this.height-this.buttonHeight+'px';

	this.commands.stop.style.top = this.height-(this.buttonHeight*2+app.gutter)+'px';
	this.commands.home.style.top = this.height-(this.buttonHeight*2+app.gutter)+'px';

	this.commands.stop.style.left = '0px';
	this.commands.stop.style.height = this.buttonHeight+'px';
	this.commands.stop.style.width = this.buttonWidth+'px';
	this.commands.stop.style.lineHeight = this.buttonHeight*.9+'px';
	this.commands.stop.style.fontSize = this.buttonHeight*.5+'px';

	this.commands.home.style.left = this.buttonWidth+app.gutter+'px';
	this.commands.home.style.height = this.buttonHeight+'px';
	this.commands.home.style.width = this.buttonWidth+'px';
	this.commands.home.style.lineHeight = this.buttonHeight*.9+'px';
	this.commands.home.style.fontSize = this.buttonHeight*.5+'px';

	this.commands.save.style.left = '0px';
	this.commands.save.style.height = this.buttonHeight+'px';
	this.commands.save.style.width = this.buttonWidth*2+app.gutter+'px';
	this.commands.save.style.lineHeight = this.buttonHeight*.9+'px';
	this.commands.save.style.fontSize = this.buttonHeight*.5+'px';

	this.ingredientDiv.style.width = this.buttonWidth*2+app.gutter+'px';
	this.ingredientDiv.style.height = this.buttonHeight+'px';
	this.ingredientDiv.style.left = '0px';
	this.ingredientDiv.style.top = '0px';
	this.ingredientDiv.style.lineHeight = this.buttonHeight*.9+'px';
	this.ingredientDiv.style.fontSize = this.buttonHeight*.5+'px';

	var leftPos = {
		'aspirate' : 0,
		'blowout' : 1,
		'droptip' : 2
	};

	var pipetteWidth = ((this.buttonWidth*2+app.gutter)+(app.gutter*-2))/3;

	for(var n in this.pipetteButton){
		this.pipetteButton[n].style.width = pipetteWidth+'px';
		this.pipetteButton[n].style.height = this.buttonHeight+'px';
		this.pipetteButton[n].style.top = this.height-(this.buttonHeight*3+app.gutter*2)+'px';
		this.pipetteButton[n].style.left = (pipetteWidth*leftPos[n])+(app.gutter*leftPos[n])+'px';
		this.pipetteButton[n].style.fontSize = this.buttonHeight*.5+'px';
		this.pipetteButton[n].style.lineHeight = this.buttonHeight+'px';
		this.div.appendChild(this.pipetteButton[n]);
	}

	this.updateSliderPositions();
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////