////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

function Job(parent){
	this.parent = parent;

	this.div = document.getElementById('job_container');

	this.jobScroller = document.getElementById('jobScroller');
	this.jobList = document.getElementById('jobList');
	this.jobAdder = document.getElementById('jobAdder');

	this.newButton = undefined;
	this.saveButton = undefined;
	this.loadButton = undefined;

	this.viewScale = 1;
	this.visible = false;

	this.loadScreen = undefined;
	this.currentFileName = undefined;

	this.clickHoldTime = 50;
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Job.prototype.update = function(){
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Job.prototype.setup = function(){
	var self = this;

	this.div.style.backgroundColor = app.colors.grey;
	this.jobAdder.style.backgroundColor = app.colors.lightGrey;

	this.newButton = document.createElement('div');
	this.newButton.innerHTML = 'Clear';
	this.newButton.className = 'abs command';
	this.newButton.style.color = 'white';
	this.newButton.style.backgroundColor = app.colors.red;

	var self = this;
	this.newButton.addEventListener('click',function(e){
		if(e) e.preventDefault();
		app.protocol = [];
		self.jobList.innerHTML = '';
	},false);

	this.saveButton = document.createElement('div');
	this.saveButton.innerHTML = 'Save';
	this.saveButton.className = 'abs command';
	this.saveButton.style.color = 'white';
	this.saveButton.style.backgroundColor = app.colors.lightGrey;

	this.loadButton = document.createElement('div');
	this.loadButton.innerHTML = 'Load';
	this.loadButton.className = 'abs command';
	this.loadButton.style.color = 'white';
	this.loadButton.style.backgroundColor = app.colors.lightGrey;

	this.div.appendChild(this.newButton);
	this.div.appendChild(this.saveButton);
	this.div.appendChild(this.loadButton);

	this.div.addEventListener('webkitTransitionEnd',function(event){
		event.preventDefault();
		if(self.visible){
			self.jobList.style.display = 'block';
		}
	},false);

	this.jobAdder.addEventListener('click',function(e){
		e.preventDefault();
		self.addStep();
	},false);

	this.loadScreen = document.createElement('div');
	this.loadScreen.className = 'abs';
	this.loadScreen.style.backgroundColor = app.colors.lightGrey;
	this.loadScreen.style.display = 'none';

	var fileSelect = document.createElement('select');
	fileSelect.className = 'command fileSelect';
	fileSelect.style.float = 'left';
	fileSelect.style.color = app.colors.grey;
	fileSelect.style.backgroundColor = 'white';
	fileSelect.style.width = '60%';
	fileSelect.style.height = '55%';
	fileSelect.style.marginLeft = '5%';
	fileSelect.style.marginTop = '5%';

	var loadFileButton = document.createElement('div');
	loadFileButton.className = 'command loadFileButton';
	loadFileButton.style.float = 'right';
	loadFileButton.style.width = '10%';
	loadFileButton.style.marginRight = '5%';
	loadFileButton.style.marginTop = '5%';
	loadFileButton.style.backgroundColor = app.colors.grey;
	loadFileButton.style.color = app.colors.green;
	loadFileButton.innerHTML = 'L';

	loadFileButton.addEventListener('click',function(e){
		e.preventDefault();
		app.loadFile(fileSelect.value);
		self.loadScreen.style.display = 'none';
	},false);

	var cancelLoadButton = document.createElement('div');
	cancelLoadButton.className = 'command cancelLoadButton';
	cancelLoadButton.style.float = 'right';
	cancelLoadButton.style.width = '10%';
	cancelLoadButton.style.marginRight = '5%';
	cancelLoadButton.style.marginTop = '5%';
	cancelLoadButton.style.backgroundColor = app.colors.grey;
	cancelLoadButton.style.color = app.colors.red;
	cancelLoadButton.innerHTML = 'X';

	cancelLoadButton.addEventListener('click',function(e){
		e.preventDefault();
		self.loadScreen.style.display = 'none';
	},false);

	this.loadScreen.appendChild(fileSelect);
	this.loadScreen.appendChild(loadFileButton);
	this.loadScreen.appendChild(cancelLoadButton);

	this.loadButton.addEventListener('click',function(e){
		e.preventDefault();
		self.listfiles();
	},false);

	this.saveButton.addEventListener('click',function(e){
		e.preventDefault();
		self.saveScreen.style.display = 'block';
	},false);

	this.div.appendChild(this.loadScreen);

	this.saveScreen = document.createElement('div');
	this.saveScreen.className = 'abs';
	this.saveScreen.style.backgroundColor = app.colors.lightGrey;
	this.saveScreen.style.display = 'none';

	var saveText = document.createElement('input');
	saveText.className = 'command saveText';
	saveText.style.textAlign = 'left';
	saveText.style.backgroundColor = 'white';
	saveText.style.float = 'left';
	saveText.style.color = app.colors.grey;
	saveText.style.width = '60%';
	saveText.style.marginLeft = '5%';
	saveText.style.marginTop = '5%';

	var saveFileButton = document.createElement('div');
	saveFileButton.className = 'command saveFileButton';
	saveFileButton.style.float = 'right';
	saveFileButton.style.width = '10%';
	saveFileButton.style.marginRight = '5%';
	saveFileButton.style.marginTop = '5%';
	saveFileButton.style.backgroundColor = app.colors.grey;
	saveFileButton.style.color = app.colors.green;
	saveFileButton.innerHTML = 'S';

	saveFileButton.addEventListener('click',function(e){
		e.preventDefault();
		app.saveFile(saveText.value);
		self.saveScreen.style.display = 'none';
	},false);

	var cancelFileButton = document.createElement('div');
	cancelFileButton.className = 'command cancelFileButton';
	cancelFileButton.style.float = 'right';
	cancelFileButton.style.width = '10%';
	cancelFileButton.style.marginRight = '5%';
	cancelFileButton.style.marginTop = '5%';
	cancelFileButton.style.backgroundColor = app.colors.grey;
	cancelFileButton.style.color = app.colors.red;
	cancelFileButton.innerHTML = 'X';

	cancelFileButton.addEventListener('click',function(e){
		e.preventDefault();
		saveText.value = '';
		self.saveScreen.style.display = 'none';
	},false);

	this.saveScreen.appendChild(saveText);
	this.saveScreen.appendChild(saveFileButton);
	this.saveScreen.appendChild(cancelFileButton);

	this.div.appendChild(this.saveScreen);
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Job.prototype.listfiles = function(){
	var self = this;
	var fileSelect = this.loadScreen.getElementsByClassName('fileSelect')[0];
	app.backend.listfiles(function(nameArray){
		fileSelect.innerHTML = '';
		for(var i=0;i<nameArray.length;i++){

			var tempOption = document.createElement('option');
			tempOption.value = nameArray[i];
			tempOption.innerHTML = nameArray[i];

			fileSelect.appendChild(tempOption);
		}

		self.loadScreen.style.display = 'block';
	});
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Job.prototype.addStep = function(loadedStep){

	var self = this;

	var buttonHeight = app.pages.jog.mixer.buttonHeight;
	var fontSize = buttonHeight*.45;
	var lineHeight = fontSize;

	var newStep = {
		'div' : undefined,
		'alive' : true
	};

	// main div for holding entire step

	var stepDiv = document.createElement('div');
	stepDiv.className = 'ingredient opacityFade';
	setTimeout(function(){
		stepDiv.style.opacity = 1.0;
	},100);
	stepDiv.style.height = buttonHeight*1+app.borderThickness*2+'px';
	stepDiv.style.width = app.width+'px';
	stepDiv.style.marginLeft = app.gutter+'px';
	stepDiv.style.backgroundColor = app.colors.grey;

	var newDiv = document.createElement('div');
	newDiv.className = 'holder';
	newDiv.style.width = app.width-(app.borderThickness*2)-app.gutter*3+'px';
	newDiv.style.marginLeft = app.gutter*3+'px';
	newDiv.style.height = buttonHeight*1+'px';
	newDiv.style.border = 'solid '+app.borderThickness+'px '+app.colors.grey;

	var numberLabel = document.createElement('div');
	numberLabel.className = 'command numberLabel';
	numberLabel.style.float = 'left';
	numberLabel.style.fontSize = fontSize+'px';
	numberLabel.style.lineHeight = lineHeight*1.4+'px';
	numberLabel.style.height = fontSize*1.5+'px';
	numberLabel.style.width = fontSize*1.5+'px';
	numberLabel.style.marginTop = '3%';
	numberLabel.style.borderRadius = '50%';
	numberLabel.style.border = 'solid '+app.borderThickness+'px white';
	numberLabel.innerHTML = app.protocol.length+1;

	newStep.touched = false;
	newStep.shouldCopy = false;
	var clickTimeout = undefined;

	function onTouch(e){
		if(e) e.preventDefault();
		clickTimeout = setTimeout(function(){
			newStep.touched = true;
			newStep.shouldCopy = true;
			clickTimeout = setTimeout(function(){
				newStep.shouldCopy = false;
			},self.clickHoldTime*3);
		},self.clickHoldTime);
		newDiv.className = 'holder fadeColor';
		newDiv.style.borderColor = app.colors.red;
	}

	function onMove(e){
		if(e) e.preventDefault();
		if(newStep.touched){
			newStep.touched = false;
			newStep.shouldCopy = false;
			if(clickTimeout) clearInterval(clickTimeout);
			newDiv.className = 'holder';
			newDiv.style.borderColor = app.colors.grey;
		}
	}

	function onRelease(e){
		if(e) e.preventDefault();
		newDiv.className = 'holder';
		newDiv.style.borderColor = app.colors.grey;
		if(newStep.touched && newStep.shouldCopy){
			self.copyStep(newStep);
		}
		if(clickTimeout) clearInterval(clickTimeout);
		newStep.touched = false;
		newStep.shouldCopy = false;
	}

	newDiv.addEventListener('webkitTransitionEnd',function(event){
		event.preventDefault();
		if(newStep.touched){
			self.eraseStep(newStep);
		}
	},false);

	if(app.mouse) numberLabel.addEventListener('mousedown',onTouch,false);
	else numberLabel.addEventListener('touchstart',onTouch,false);
	if(app.mouse) numberLabel.addEventListener('mousemove',onMove,false);
	else numberLabel.addEventListener('touchmove',onMove,false);
	if(app.mouse) numberLabel.addEventListener('mouseup',onRelease,false);
	else numberLabel.addEventListener('touchend',onRelease,false);

	stepDiv.appendChild(numberLabel);

	// INGREDIENT is a drop down that selects which mapped ingredient to use

	var ingredientDiv = document.createElement('div');
	ingredientDiv.className = 'command';
	ingredientDiv.style.overflow = 'hidden';
	ingredientDiv.style.color = app.colors.grey;
	ingredientDiv.style.float = 'left';
	ingredientDiv.style.height = '100%';
	ingredientDiv.style.width = '47%';
	ingredientDiv.style.backgroundColor = app.colors.grey;

	var ingredientSelect = document.createElement('select');
	ingredientSelect.className = 'command ingredientList';
	ingredientSelect.style.height = '50%';
	ingredientSelect.style.width = '100%';
	ingredientSelect.style.backgroundColor = app.colors.grey;
	ingredientSelect.style.marginTop = '12%';
	ingredientSelect.style.color = app.colors.green;
	ingredientSelect.style.fontSize = fontSize*.8+'px';
	ingredientSelect.style.lineHeight = lineHeight+'px';

	for(var i in app.ingredients){
		var tempOption = document.createElement('option');
		tempOption.value = i;
		tempOption.innerHTML = app.ingredients[i].name;
		ingredientSelect.appendChild(tempOption);
	}

	if(loadedStep) ingredientSelect.value = loadedStep.ingredient;

	ingredientDiv.appendChild(ingredientSelect);
	newDiv.appendChild(ingredientDiv);

	// TRIGGER div, sets TIME stuff

	var triggerDiv = document.createElement('div');
	triggerDiv.className = 'command';
	triggerDiv.style.overflow = 'hidden';
	triggerDiv.style.color = app.colors.grey;
	triggerDiv.style.float = 'left';
	triggerDiv.style.height = '100%';
	triggerDiv.style.width = '20%';
	triggerDiv.style.backgroundColor = app.colors.grey;
	triggerDiv.style.fontSize = fontSize/2+'px';
	triggerDiv.style.lineHeight = lineHeight+'px';

	var timeInput = document.createElement('input');
	timeInput.addEventListener('input',function(e){
		if(e) e.preventDefault();
		//
	},false);
	timeInput.type = 'input';
	timeInput.value = 0;
	timeInput.className = 'command timeValue';
	timeInput.style.height = '50%';
	timeInput.style.width = '100%';
	//timeInput.style.marginTop = -fontSize*.2+'px';
	timeInput.style.backgroundColor = app.colors.grey;
	timeInput.style.color = 'white';
	timeInput.style.fontSize = fontSize*.75+'px';
	timeInput.style.lineHeight = lineHeight*.75+'px';

	var select = document.createElement('select');
	select.className = 'command timeSelect';
	select.style.height = '50%';
	select.style.width = '70%';
	select.style.marginLeft = '22%';
	select.style.backgroundColor = app.colors.grey;
	//select.style.paddingLeft = '33%';
	select.style.color = 'white';
	select.style.fontSize = fontSize*.75+'px';
	select.style.lineHeight = lineHeight*.75+'px';

	var times = ['ms','sec','min'];

	for(var i=0;i<times.length;i++){
		var tempOption = document.createElement('option');
		tempOption.value = times[i];
		tempOption.innerHTML = times[i];
		select.appendChild(tempOption);
	}

	if(loadedStep){
		var tempTime = loadedStep.trigger.value;
		if(tempTime<1000){
			timeInput.value = tempTime;
			select.value = 'ms';
		}
		else if(tempTime<60000){
			timeInput.value = tempTime/1000;
			select.value = 'sec';
		}
		else{
			timeInput.value = tempTime/60000;
			select.value = 'min';
		}
	}

	triggerDiv.appendChild(timeInput);
	triggerDiv.appendChild(select);

	newDiv.appendChild(triggerDiv);

	// ACTION sets the amount of liquid moved, or if it's a gripper

	var actionDiv = document.createElement('div');
	actionDiv.className = 'command';
	actionDiv.style.overflow = 'hidden';
	//actionDiv.style.color = app.colors.blue;
	actionDiv.style.float = 'left';
	actionDiv.style.height = '100%';
	actionDiv.style.width = '20%';
	actionDiv.style.backgroundColor = app.colors.grey;
	// actionDiv.style.fontSize = fontSize/2+'px';
	// actionDiv.style.lineHeight = lineHeight+'px';

	var microLiters = document.createElement('input');
	microLiters.addEventListener('input',function(e){
		if(e) e.preventDefault();
	},false);
	microLiters.type = 'input';
	microLiters.value = 0;
	microLiters.className = 'command actionVal';
	microLiters.style.height = '50%';
	microLiters.style.width = '100%';
	//microLiters.style.marginTop = -fontSize*.2+'px';
	microLiters.style.backgroundColor = app.colors.grey
	//microLiters.style.color = app.colors.blue;
	microLiters.style.fontSize = fontSize*.75+'px';
	microLiters.style.lineHeight = lineHeight*.75+'px';

	var actionSelect = document.createElement('select');
	actionSelect.className = 'command actionSelect';
	actionSelect.style.height = '50%';
	actionSelect.style.width = '40%';
	actionSelect.style.marginLeft = '33%';
	actionSelect.style.backgroundColor = app.colors.grey;
	//actionSelect.style.color = app.colors.blue;
	actionSelect.style.fontSize = fontSize*.75+'px';
	actionSelect.style.lineHeight = lineHeight*.75+'px';

	actionSelect.addEventListener('change',function(e){
		e.preventDefault();
		for(var i=0;i<actionOptions.length;i++){
			if(this.value===actionOptions[i]) numberLabel.style.borderColor = actionColors[i];
		}
	},false);

	var actionOptions = [app.arrows.aspirate,app.arrows.blowout,app.arrows.droptip];
	var actionColors = ['white',app.colors.blue,app.colors.yellow];

	for(var i=0;i<actionOptions.length;i++){
		var tempOption = document.createElement('option');
		tempOption.value = actionOptions[i];
		tempOption.innerHTML = actionOptions[i];
		actionSelect.appendChild(tempOption);
	}

	if(loadedStep){
		console.log(loadedStep.action);
		if(loadedStep.action.aspirate!=undefined){
			microLiters.value = loadedStep.action.aspirate;
			actionSelect.value = actionOptions[0];
			numberLabel.style.borderColor = actionColors[0];
		}
		else if(loadedStep.action.blowout!=undefined){
			console.log('3');
			actionSelect.value = actionOptions[1];
			numberLabel.style.borderColor = actionColors[1];
		}
		else if(loadedStep.action.droptip!=undefined){
			console.log('four');
			actionSelect.value = actionOptions[2];
			numberLabel.style.borderColor = actionColors[2];
		}
	}

	actionDiv.appendChild(microLiters);
	actionDiv.appendChild(actionSelect);

	newDiv.appendChild(actionDiv);

	// NAVIGATION ARROWS
	var arrowDiv = document.createElement('div');
	arrowDiv.className = 'command';
	arrowDiv.style.overflow = 'hidden';
	arrowDiv.style.color = app.colors.grey;
	arrowDiv.style.float = 'left';
	arrowDiv.style.height = '100%';
	arrowDiv.style.width = '13%';
	//arrowDiv.style.backgroundColor = app.colors.green;

	var upArrow = document.createElement('img');
	upArrow.className = 'command upArrow';
	upArrow.style.height = '50%';
	upArrow.style.width = '100%';
	upArrow.style.color = app.colors.yellow;
	upArrow.style.fontSize = fontSize+'px';
	upArrow.style.lineHeight = lineHeight*1.7+'px';
	//upArrow.innerHTML = '&#8593;';
	upArrow.src = './img/arrow_up.png';

	var downArrow = document.createElement('img');
	downArrow.className = 'command downArrow';
	downArrow.style.height = '50%';
	downArrow.style.width = '100%';
	downArrow.style.color = app.colors.yellow;
	downArrow.style.fontSize = fontSize+'px';
	downArrow.style.lineHeight = lineHeight*1.7+'px';
	// downArrow.innerHTML = '&#8595;';
	downArrow.src = './img/arrow_down.png';

	upArrow.addEventListener('click',function(e){
		e.preventDefault();
		self.moveStep(newStep,-1);
	},false);
	downArrow.addEventListener('click',function(e){
		e.preventDefault();
		self.moveStep(newStep,1);
	},false);

	arrowDiv.appendChild(upArrow);
	arrowDiv.appendChild(downArrow);
	newDiv.appendChild(arrowDiv);

	// add it all in the end

	newStep.div = stepDiv;
	app.protocol.push(newStep);

	stepDiv.appendChild(newDiv);
	this.jobList.appendChild(stepDiv);
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Job.prototype.greenFade = function(step){
	var b = step.div.getElementsByClassName('holder')[0];

	if(b.timeOut) clearTimeout(b.timeOut);

	var name = b.className;
	var newClass = ' fadeColor';
	var i = name.indexOf(newClass);
	if(i>=0) b.className = b.className.replace( /(?:^|\s)fadeColor(?!\S)/ , '' );

	b.timeOut = setTimeout(function(){
		b.style.borderColor = app.colors.green;
		b.timeOut = setTimeout(function(){
			b.className += ' fadeColor';
			b.timeOut = setTimeout(function(){
				b.style.borderColor = app.colors.grey;
			},40);
		},40);
	},40);
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Job.prototype.moveStep = function(step,dir){

	function swapArrayElements(array_object, index_a, index_b) {
	    var temp = array_object[index_a];
	    array_object[index_a] = array_object[index_b];
	    array_object[index_b] = temp;
	}

	var test = false;

	for(var i=0;i<app.protocol.length;i++){
		if(app.protocol[i]===step && i+dir>=0 && i+dir<app.protocol.length){

			swapArrayElements(app.protocol,i,i+dir);
			this.greenFade(app.protocol[i]);
			this.greenFade(app.protocol[i+dir]);

			if(dir<0){
				var prevDiv = step.div.previousSibling;
				if(prevDiv){
					this.jobList.insertBefore(step.div,prevDiv);
					test = true;
				}
			}
			else if(dir>0){
				var nextDiv = step.div.nextSibling;
				if(nextDiv){
					nextDiv = nextDiv.nextSibling;
					if(nextDiv){
						this.jobList.insertBefore(step.div,nextDiv);
						test = true;
					}
					else{
						this.jobList.appendChild(step.div);
						test = true;
					}
				}
			}
			break;
		}
	}
	for(var i=0;i<app.protocol.length;i++){
		app.protocol[i].div.getElementsByClassName('numberLabel')[0].innerHTML = i+1;
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Job.prototype.copyStep = function(step){
	var self = this;

	if(step.alive){

		var newStep = {};
		for(var n in step){
			if(n!='div') newStep[n] = step[n];
		}

		var cloneDiv = step.div.cloneNode(true);
		cloneDiv.style.opacity = 0;
		setTimeout(function(){
			cloneDiv.style.opacity = 1;
		},100);
		newStep.div = cloneDiv;

		var borderDiv = cloneDiv.getElementsByClassName('holder');
		var copyButton = cloneDiv.getElementsByClassName('numberLabel');

		if(copyButton[0] && borderDiv[0]){
			var c = copyButton[0];
			var b = borderDiv[0];

			newStep.touched = false;
			newStep.shouldCopy = false;
			var clickTimeout = undefined;

			function onTouch(e){
				if(e) e.preventDefault();
				clickTimeout = setTimeout(function(){
					newStep.touched = true;
					newStep.shouldCopy = true;
					clickTimeout = setTimeout(function(){
						newStep.shouldCopy = false;
					},self.clickHoldTime*3);
				},self.clickHoldTime);
				b.className = 'holder fadeColor';
				b.style.borderColor = app.colors.red;
			}

			function onMove(e){
				if(e) e.preventDefault();
				newStep.touched = false;
				newStep.shouldCopy = false;
				if(clickTimeout) clearInterval(clickTimeout);
				b.className = 'holder';
				b.style.borderColor = app.colors.grey;
			}

			function onRelease(e){
				if(e) e.preventDefault();
				b.className = 'holder';
				b.style.borderColor = app.colors.grey;
				if(newStep.touched && newStep.shouldCopy){
					self.copyStep(newStep);
				}
				if(clickTimeout) clearInterval(clickTimeout);
				newStep.touched = false;
				newStep.shouldCopy = false;
			}

			b.addEventListener('webkitTransitionEnd',function(event){
				event.preventDefault();
				if(newStep.touched){
					self.eraseStep(newStep);
				}
			},false);

			if(app.mouse) c.addEventListener('mousedown',onTouch,false);
			else c.addEventListener('touchstart',onTouch,false);
			if(app.mouse) c.addEventListener('mousemove',onMove,false);
			else c.addEventListener('touchmove',onMove,false);
			if(app.mouse) c.addEventListener('mouseup',onRelease,false);
			else c.addEventListener('touchend',onRelease,false);

			var oldTime = step.div.getElementsByClassName('timeSelect')[0].value;
			newStep.div.getElementsByClassName('timeSelect')[0].value = oldTime;
			var oldAction = step.div.getElementsByClassName('actionSelect')[0].value;
			newStep.div.getElementsByClassName('actionSelect')[0].value = oldAction;
			var oldIngredient = step.div.getElementsByClassName('ingredientList')[0].value;
			newStep.div.getElementsByClassName('ingredientList')[0].value = oldIngredient;

			newStep.div.getElementsByClassName('upArrow')[0].addEventListener('click',function(e){
				e.preventDefault();
				self.moveStep(newStep,-1);
			},false);
			newStep.div.getElementsByClassName('downArrow')[0].addEventListener('click',function(e){
				e.preventDefault();
				self.moveStep(newStep,1);
			},false);
		}
		for(var i=0;i<app.protocol.length;i++){
			if(app.protocol[i]===step){
				if(i+1<app.protocol.length){
					app.protocol.splice(i+1,0,newStep);
					this.jobList.insertBefore(newStep.div,step.div.nextSibling);
				}
				else{
					app.protocol.push(newStep);
					this.jobList.appendChild(newStep.div);
				}
			}
			app.protocol[i].div.getElementsByClassName('numberLabel')[0].innerHTML = i+1;
		}

		self.greenFade(newStep);
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Job.prototype.eraseStep = function(step){
	if(step.alive){
		for(var i=0;i<app.protocol.length;i++){
			if(app.protocol[i]===step){
				step.alive = false;
				step.div.parentNode.removeChild(step.div);
				app.protocol.splice(i,1);
				i--;
			}
			else{
				var label = app.protocol[i].div.getElementsByClassName('numberLabel');
				if(label[0]) label[0].innerHTML = i+1;
			}
		}
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Job.prototype.resize = function(){

	var jobLeftPos = Math.floor(this.viewScale*app.theWidth);

	this.div.style.top = '0px';
	this.div.style.left = jobLeftPos+'px';

	var buttonWidth = (app.width-(app.gutter*2))/3;
	var buttonHeight = app.pages.jog.mixer.buttonHeight;
	var fontSize = buttonHeight*.5;
	var lineHeight = fontSize*1.9;

	this.newButton.style.width = buttonWidth+'px';
	this.newButton.style.height = buttonHeight+'px';
	this.newButton.style.left = app.gutter+'px';
	this.newButton.style.top = app.navOffset+'px';
	this.newButton.style.fontSize = fontSize+'px';
	this.newButton.style.lineHeight = lineHeight+'px';

	this.saveButton.style.width = buttonWidth+'px';
	this.saveButton.style.height = buttonHeight+'px';
	this.saveButton.style.left = buttonWidth+(app.gutter*2)+'px';
	this.saveButton.style.top = app.navOffset+'px';
	this.saveButton.style.fontSize = fontSize+'px';
	this.saveButton.style.lineHeight = lineHeight+'px';

	this.loadButton.style.width = buttonWidth+'px';
	this.loadButton.style.height = buttonHeight+'px';
	this.loadButton.style.left = (buttonWidth*2)+(app.gutter*3)+'px';
	this.loadButton.style.top = app.navOffset+'px';
	this.loadButton.style.fontSize = fontSize+'px';
	this.loadButton.style.lineHeight = lineHeight+'px';

	var scrollTop = app.navOffset+buttonHeight+app.gutter;
	var scrollHeight = app.theHeight-(scrollTop+app.gutter);

	this.jobScroller.style.left = '0px';
	this.jobScroller.style.top = scrollTop+'px';
	this.jobScroller.style.width = app.width+app.gutter*2+'px';
	this.jobScroller.style.height = scrollHeight+'px';

	this.jobList.style.width = app.width+app.gutter*2+'px';

	this.jobAdder.style.color = 'white';
	this.jobAdder.style.width = app.width/2+'px';
	this.jobAdder.style.height = buttonHeight+'px';
	this.jobAdder.style.marginLeft = app.width/4+app.gutter+'px';
	this.jobAdder.style.fontSize = fontSize+'px';
	this.jobAdder.style.lineHeight = lineHeight+'px';

	var tempSteps = this.jobList.getElementsByClassName('ingredientList');
	for(var i=0;i<tempSteps.length;i++){
		var currentValue = tempSteps[i].value;
		tempSteps[i].innerHTML = '';
		for(var n in app.ingredients){
			var tempOption = document.createElement('option');
			tempOption.value = n;
			tempOption.innerHTML = app.ingredients[n].name;
			tempSteps[i].appendChild(tempOption);
		}
		if(app.ingredients[currentValue]) tempSteps[i].value = currentValue;
		else tempSteps[i].value = n;
	}

	this.loadScreen.style.width = app.width+'px';
	this.loadScreen.style.height = buttonHeight+'px';
	this.loadScreen.style.left = app.gutter+'px';
	this.loadScreen.style.top = app.navOffset+'px';
	this.loadScreen.getElementsByClassName('fileSelect')[0].style.fontSize = fontSize*.8+'px';
	this.loadScreen.getElementsByClassName('fileSelect')[0].style.lineHeight = lineHeight*.5+'px';

	this.saveScreen.style.width = app.width+'px';
	this.saveScreen.style.height = buttonHeight+'px';
	this.saveScreen.style.left = app.gutter+'px';
	this.saveScreen.style.top = app.navOffset+'px';

	this.saveScreen.getElementsByClassName('saveText')[0].style.fontSize = fontSize*.8+'px';
	this.saveScreen.getElementsByClassName('saveText')[0].style.lineHeight = lineHeight*.5+'px';
	this.saveScreen.getElementsByClassName('saveFileButton')[0].style.fontSize = fontSize*.8+'px';
	this.saveScreen.getElementsByClassName('saveFileButton')[0].style.lineHeight = lineHeight*.6+'px';
	this.saveScreen.getElementsByClassName('cancelFileButton')[0].style.fontSize = fontSize*.8+'px';
	this.saveScreen.getElementsByClassName('cancelFileButton')[0].style.lineHeight = lineHeight*.6+'px';

	this.loadScreen.getElementsByClassName('fileSelect')[0].style.fontSize = fontSize*.8+'px';
	this.loadScreen.getElementsByClassName('fileSelect')[0].style.lineHeight = lineHeight*.5+'px';
	this.loadScreen.getElementsByClassName('loadFileButton')[0].style.fontSize = fontSize*.8+'px';
	this.loadScreen.getElementsByClassName('loadFileButton')[0].style.lineHeight = lineHeight*.6+'px';
	this.loadScreen.getElementsByClassName('cancelLoadButton')[0].style.fontSize = fontSize*.8+'px';
	this.loadScreen.getElementsByClassName('cancelLoadButton')[0].style.lineHeight = lineHeight*.6+'px';
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////