////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

function Ingredients(parent){
	this.parent = parent;

	this.div = document.getElementById('ingredients_container');
	this.pipetteDiv = document.getElementById('pipette_container');
	this.pipetteDropdown = document.getElementById('pipette_sizes');

	this.ingredientScroller = document.getElementById('ingredientScroller');
	this.ingredientAdder = document.getElementById('ingredientAdder');

	this.ingredientList = document.getElementById('ingredientList');

	this.viewScale = 1;
	this.visible = false;

	this.nameCounter = 0;
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Ingredients.prototype.update = function(){
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Ingredients.prototype.addIngredient = function(){
	var buttonHeight = app.pages.jog.mixer.buttonHeight;
	var fontSize = buttonHeight*.5;
	var lineHeight = fontSize*.9;

	this.nameCounter++;

	var newIngredient = {
		'touched' : false
	};
	newIngredient.name = 'untitled_'+this.nameCounter;
	newIngredient.x = undefined;
	newIngredient.y = undefined;
	newIngredient.z = undefined;

	var newDiv = document.createElement('div');
	newDiv.className = 'ingredient opacityFade';
	newDiv.style.width = app.width+'px';
	newDiv.style.height = buttonHeight+'px';
	newDiv.style.backgroundColor = app.colors.grey;

	setTimeout(function(){
		newDiv.style.opacity = 1;
	},100);

	var eraseButton = document.createElement('div');
	eraseButton.className = 'command';
	eraseButton.style.float = 'right';
	eraseButton.style.height = buttonHeight+'px';
	eraseButton.style.width = buttonHeight+'px';
	eraseButton.style.backgroundColor = app.colors.grey;
	eraseButton.style.color = app.colors.red;
	eraseButton.style.fontSize = fontSize*1.5+'px';
	eraseButton.style.lineHeight = lineHeight*2+'px';
	eraseButton.innerHTML = 'X';

	newDiv.appendChild(eraseButton);

	var jogButton = document.createElement('div');
	jogButton.style.float = 'right';
	jogButton.style.height = buttonHeight-app.borderThickness*2+'px';
	jogButton.style.width = buttonHeight-app.borderThickness*2+'px';
	jogButton.style.borderRadius = '50%';
	jogButton.style.backgroundColor = app.colors.grey;
	jogButton.style.border = 'solid '+app.borderThickness+'px white';

	var jogImg = document.createElement('img');
	jogImg.src = './img/jog.png';
	jogImg.style.height = '60%';
	jogImg.style.marginLeft = '21%';
	jogImg.style.marginTop = '20%';
	jogImg.className = 'jogImage';
	jogButton.appendChild(jogImg);

	newDiv.appendChild(jogButton);

	var nameText = document.createElement('input');
	nameText.addEventListener('input',function(){
		newIngredient.name = nameText.value;
	},false);
	nameText.type = 'input';
	nameText.className = 'command';
	nameText.style.color = app.colors.green;
	nameText.style.width = '49%';
	nameText.style.height = '100%';
	nameText.style.fontSize = fontSize+'px';
	nameText.style.backgroundColor = app.colors.grey;
	nameText.style.margin = '0px 0px 0px 0px';

	nameText.value = 'untitled_'+this.nameCounter;

	newDiv.appendChild(nameText);

	newIngredient.div = newDiv;

	this.ingredientList.appendChild(newDiv);
	app.ingredients[this.nameCounter] = newIngredient;

	var tempName = this.nameCounter;
	var self = this;

	var self = this;
	eraseButton.addEventListener('click',function(e){
		e.preventDefault();
		self.eraseIngredient(tempName);
	},false);

	jogButton.addEventListener('click',function(e){
		e.preventDefault();
		app.navigate('jog',tempName);
	},false);
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Ingredients.prototype.eraseIngredient = function(name){
	if(app.ingredients[name]){
		var i = app.ingredients[name];
		app.ingredients[name].div.parentNode.removeChild(i.div);
		delete app.ingredients[name];
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Ingredients.prototype.setup = function(){
	var self = this;
	this.div.style.backgroundColor = app.colors.grey;
	this.div.addEventListener('webkitTransitionEnd',function(event){
		event.preventDefault();
		if(self.visible){
			self.ingredientList.style.display = 'block';
		}
	},false);

	this.pipetteDiv.style.backgroundColor = app.colors.lightGrey;
	this.pipetteDropdown.style.color = app.colors.blue;

	var self = this;
	var onTouch = function(event){
		event.preventDefault();
		self.addIngredient();
	};

	if(true){
		this.ingredientAdder.addEventListener('click',onTouch,false);
		//this.ingredientAdder.addEventListener('mouseup',onRelease,false);
	}
	else{
		this.ingredientAdder.addEventListener('touchstart',onTouch,false);
		this.ingredientAdder.addEventListener('touchend',onRelease,false);
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Ingredients.prototype.resize = function(){

	var ingredientLeftPos = Math.floor(this.viewScale*app.theWidth);

	this.div.style.top = '0px';
	this.div.style.left = ingredientLeftPos+'px';

	var buttonHeight = app.pages.jog.mixer.buttonHeight;
	var fontSize = buttonHeight*.5;
	var lineHeight = fontSize*1.9;

	this.pipetteDiv.style.left = app.gutter+'px';
	this.pipetteDiv.style.top = app.navOffset+'px';
	this.pipetteDiv.style.width = app.width+'px';
	this.pipetteDiv.style.height = buttonHeight+'px';
	this.pipetteDiv.style.fontSize = fontSize+'px';
	this.pipetteDiv.style.lineHeight = lineHeight+'px';

	document.getElementById('pipetteLabel').style.left = app.gutter+'px';

	this.pipetteDropdown.style.left = app.width/2+app.gutter+'px';
	this.pipetteDropdown.style.top = buttonHeight*.22+'px';
	this.pipetteDropdown.style.fontSize = fontSize+'px';
	this.pipetteDropdown.style.lineHeight = lineHeight/2+'px';

	var scrollTop = app.navOffset+buttonHeight+app.gutter;
	var scrollHeight = app.theHeight-(scrollTop+app.gutter);

	this.ingredientScroller.style.left = app.gutter+'px';
	this.ingredientScroller.style.top = scrollTop+'px';
	this.ingredientScroller.style.width = app.width+'px';
	this.ingredientScroller.style.height = scrollHeight+'px';

	this.ingredientList.style.width = app.width+'px';

	this.ingredientAdder.style.backgroundColor = app.colors.lightGrey;
	this.ingredientAdder.style.color = 'white';
	this.ingredientAdder.style.width = app.width/2+'px';
	this.ingredientAdder.style.marginLeft = app.width/4+'px';
	this.ingredientAdder.style.height = buttonHeight+'px';
	this.ingredientAdder.style.fontSize = fontSize+'px';
	this.ingredientAdder.style.lineHeight = lineHeight+'px';

	var tempIngredients = this.ingredientList.children;
	for(var i=0;i<tempIngredients.length;i++){
		tempIngredients[i].style.height = buttonHeight+'px';
		tempIngredients[i].style.width = app.width+'px';
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////