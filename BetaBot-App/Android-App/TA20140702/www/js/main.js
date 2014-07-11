////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

var app = {

	// app global variables

	mouse : false,

	stepAmount : 0.003, // step size during jog button presses (percentage 0-1)

	opacityFadeTime : 300,

	backend: undefined,

	robot: {
		'x': undefined,
		'y': undefined,
		'z': undefined,
		'a': undefined,
		'listening':undefined,
		'power':undefined
	},

	theWidth: 0,
	theHeight: 0,

	borderThickness : 2,

	width : 0,
	height : 0,

	colors : {
		'green' : 'rgb(17,232,90)',
		'red' : 'rgb(255,23,79)',
		'blue' : 'rgb(31,170,255)',
		'yellow' : 'rgb(239,255,104)',
		'purple' : 'rgb(131,67,232)',

		'grey' : 'rgb(100,100,100)',
		'lightGrey' : 'rgb(140,140,140)'
	},

	arrows : {
		'aspirate' : '&#8593;&#8593;',
		'blowout' : '&#8595;&#8595;',
		'droptip' : '&#10094;&#10095;'
	},

	pages : {
		'home' : undefined,
		'jog' : undefined,
		'ingredients' : undefined,
		'job' : undefined
	},

	navBar : undefined,
	navIndicator : undefined,
	navUnits : {
		'home' : undefined,
		'jog' : undefined,
		'ingredients' : undefined,
		'job' : undefined
	},

	ingredients : {},
	protocol : [],

	////////////////////////////////////
	////////////////////////////////////
	////////////////////////////////////

	setup : function(){
		this.isSetup = true;

		var self = this;

		self.backend = new Backend(this);

		self.pages.jog = new Jog(this);
		self.pages.jog.setup();

		self.pages.ingredients = new Ingredients(this);
		self.pages.ingredients.setup();

		self.pages.job = new Job(this);
		self.pages.job.setup();

		self.pages.home = new Home(this);
		self.pages.home.setup();

		self.navBar = document.getElementById('navBar');

		for(var n in self.navUnits){
			self.navUnits[n] = document.getElementById(n+'Nav');
			var onNavClick = (function(){
				var name = n;
				return function(){
					self.navigate(name);
				};
			})();
			if(this.mouse) self.navUnits[n].addEventListener('mousedown',onNavClick,false);
			else self.navUnits[n].addEventListener('touchstart',onNavClick,false);

		}

		self.navIndicator = document.createElement('div');
		self.navIndicator.className = 'abs slider';
		self.navIndicator.style.backgroundColor = self.colors.blue;
		self.navIndicator.style.left = '6.25%';
		self.navBar.appendChild(self.navIndicator);
	},

	////////////////////////////////////
	////////////////////////////////////
	////////////////////////////////////

	saveFile : function(name){
		this.backend.save( name,this.prepareJob(true) );
	},

	////////////////////////////////////
	////////////////////////////////////
	////////////////////////////////////

	loadFile : function(name){
		this.backend.load(name);
	},

	////////////////////////////////////
	////////////////////////////////////
	////////////////////////////////////

	prepareJob : function(isSaving){

		var theJob = {
			'protocol' : [],
			'ingredients' : [],
			'pipette' : undefined
		}

		var failed = false;

		theJob.pipette= document.getElementById('pipette_sizes').value;

		for(var i in this.ingredients){
			var tempIngredient = {};
			tempIngredient.name = i;
			if(this.ingredients[i].x===undefined || this.ingredients[i].y===undefined || this.ingredients[i].z===undefined){
				console.log('ingredient '+this.ingredients[i].name+' must be set');
				failed = true;
			}
			else{
				tempIngredient.x = this.ingredients[i].x*app.backend.max.x;
				tempIngredient.y = this.ingredients[i].y*app.backend.max.y;
				tempIngredient.z = this.ingredients[i].z*app.backend.max.z;
			}
			theJob.ingredients.push(tempIngredient);
		}
		if(!failed || isSaving){
			for(var i=0;i<this.protocol.length;i++){

				var tempStep = {
					'trigger' : {
						'type' : 'time',
						'value' : undefined
					},
					'action' : {},
					'ingredient' : undefined
				};

				var div = this.protocol[i].div;

				var timeVal = Number(div.getElementsByClassName('timeValue')[0].value);
				var timeType = div.getElementsByClassName('timeSelect')[0].value;

				var actionVal = Number(div.getElementsByClassName('actionVal')[0].value);
				var actionType = div.getElementsByClassName('actionSelect')[0].value;

				for(var a in this.arrows){
					if(this.arrows[a]===actionType){
						actionType = a;
						break;
					}
				}

				var ingTag = div.getElementsByClassName('ingredientList')[0].value; // not Name

				if(isNaN(timeVal) || isNaN(actionVal)){
					failed = true;
					console.log('Error on Job step '+(i+1));
				}
				else{
					//scale it to milliseconds
					var mult = 1;
					if(timeType==='ms') mult = 1;
					else if(timeType==='sec') mult = 1000;
					else if(timeType==='min') mult = 60000;
					timeVal *= mult; // milliseconds
				}

				if(!failed || isSaving){
					tempStep.trigger.value = Math.floor(timeVal);
					tempStep.action[actionType] = Math.floor(actionVal);
					tempStep.ingredient = ingTag

					theJob.protocol.push(tempStep);
				}
			}
		}

		return theJob;
	},

	////////////////////////////////////
	////////////////////////////////////
	////////////////////////////////////

	navigate : function(string,ingredient){

		if(string!=this.currentNavigation){

			this.pages.ingredients.ingredientList.style.display = 'none';
			this.pages.ingredients.visible = false;

			this.pages.job.jobList.style.display = 'none';
			this.pages.job.visible = false;

			var navOffset = 6.25;

			if(string==='home'){
				this.pages.home.viewScale = 0;
				this.pages.jog.viewScale = 1;
				this.pages.ingredients.viewScale = 2;
				this.pages.job.viewScale = 3;
				this.navIndicator.style.left = navOffset+'%';
			}
			else if(string==='jog'){
				this.pages.home.viewScale = -1;
				this.pages.jog.viewScale = 0;
				this.pages.ingredients.viewScale = 1;
				this.pages.job.viewScale = 2;
				this.navIndicator.style.left = 25+navOffset+'%';

				this.pages.jog.setIngredient(ingredient);
			}
			else if(string==='ingredients'){
				this.pages.home.viewScale = -2;
				this.pages.jog.viewScale = -1;
				this.pages.ingredients.viewScale = 0;
				this.pages.job.viewScale = 1;
				this.navIndicator.style.left = 50+navOffset+'%';
				this.pages.ingredients.visible = true;
			}
			else if(string==='job'){
				this.pages.home.viewScale = -3;
				this.pages.jog.viewScale = -2;
				this.pages.ingredients.viewScale = -1;
				this.pages.job.viewScale = 0;
				this.navIndicator.style.left = 75+navOffset+'%';
				this.pages.job.visible = true;
			}
			else{
				console.log(string);
			}

			this.resize(true);

		}

		this.currentNavigation = string;
	},

	////////////////////////////////////
	////////////////////////////////////
	////////////////////////////////////

	updateListening : function(state){
		// if(state==1) console.log('listening');
		// else console.log('ignoring...');
	},

	////////////////////////////////////
	////////////////////////////////////
	////////////////////////////////////

	updatePower : function(state){
		//
	},

	////////////////////////////////////
	////////////////////////////////////
	////////////////////////////////////

	onRobotUpdate : function(msg){

		if(msg.hasOwnProperty('listening') && this.robot.listening!=msg.listening){
			this.updateListening(msg.listening);
		}
		if(msg.hasOwnProperty('power') && this.robot.power!=msg.power){
			this.updatePower(msg.power);
		}

		var changed = false;
		for(var n in msg){
			if(msg[n]!=this.robot[n]) changed = true;
			this.robot[n] = msg[n];
		}

		if(changed) {
			for(var n in this.pages){
				this.pages[n].update();
			}
		}
	},

	////////////////////////////////////
	////////////////////////////////////
	////////////////////////////////////

	resize : function(forced){

		var w = window.innerWidth;
		var h = window.innerHeight;

		if(w!=this.theWidth || this.theHeight!=h){

			this.theWidth = w;
			if(h>this.theHeight) this.theHeight = h;

			this.gutter = Math.floor(this.theWidth*.05);

			this.borderThickness = Math.max(this.gutter/5,1);

			this.width = this.theWidth-(this.gutter*2);
			this.height = this.theHeight-(this.gutter*2)-this.borderThickness;

			this.left = (this.theWidth/2)-(this.theWidth/2);
			this.right = this.left+this.theWidth;

			this.top = 0;
			this.bottom = this.h;

			var container = document.getElementById('container');
			container.style.backgroundColor = 'purple';
			container.style.left = this.left+'px';
			container.style.top = this.top+'px';
			container.style.width = this.theWidth+'px';
			container.style.height = this.theHeight+'px';

			this.navBar.style.left = this.gutter+'px';
			this.navBar.style.top = this.gutter+'px';
			this.navBar.style.width = this.width+'px';
			this.navBar.style.height = this.gutter*2+'px';

			var offset = 6.25;
			this.navUnits.home.style.left = offset+'%';
			this.navUnits.jog.style.left = 25+offset+'%';
			this.navUnits.ingredients.style.left = 50+offset+'%';
			this.navUnits.job.style.left = 75+offset+'%';

			this.navIndicator.style.top = this.gutter*2+'px';
			this.navIndicator.style.width = '10%';
			this.navIndicator.style.height = app.borderThickness+'px';

			this.navOffset = this.gutter*4;

		}

		if(this.pages.jog){
			this.pages.jog.resize();
			this.pages.home.resize();
			this.pages.ingredients.resize();
			this.pages.job.resize();
		}
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////