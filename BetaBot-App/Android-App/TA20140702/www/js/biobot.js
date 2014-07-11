////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

function BioBot(parent){
	this.parent = parent;

	this.div = document.getElementById('biobot_container');

	this.searchButton = document.getElementById('searchButton');
	this.linkButton = document.getElementById('linkButton');
	this.homeButton = document.getElementById('homeButton');

	this.deviceList = document.getElementById('deviceList');

	this.viewScale = 0;
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

BioBot.prototype.update = function(){
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

BioBot.prototype.initDisconnect = function(){
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

BioBot.prototype.listDevices = function(){
	while(this.deviceList.firstChild){
		this.deviceList.removeChild(this.deviceList.firstChild);
	}
	function onSuccess(devices){
		for(var i=0;i<devices.length;i++){
			var opt = document.createElement('option');
			opt.value = devices[i].address;
			opt.innerHTML = devices[i].name;
			this.deviceList.appendChild(opt);
		}
	}
	function onError(){
		console.log('error listing devices');
	}
	app.backend.listDevices(onSuccess,onError);
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

BioBot.prototype.onConnect = function(){
	console.log('YAYAYA CONNECTED in the page!!');
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

BioBot.prototype.setup = function(){
	this.div.style.backgroundColor = app.colors.biobot;

	this.searchButton.style.backgroundColor = app.colors.grey;

	var self = this;

	function initList(){
		if(self.connected) self.initDisconnect();
		else self.listDevices();
	}

	function initConnect(){
		var address = self.deviceList.value;
		console.log(address);
		app.backend.connect(address,self.onConnect);
	}

	function initHoming(){
		app.backend.home();
	}

	if(app.mouse){
		this.searchButton.addEventListener('mousedown',initList,false);
		this.linkButton.addEventListener('mousedown',initConnect,false);
		this.homeButton.addEventListener('mousedown',initHoming,false);
	}
	else{
		this.searchButton.addEventListener('touchstart',initList,false);
		this.linkButton.addEventListener('touchstart',initConnect,false);
		this.homeButton.addEventListener('touchstart',initHoming,false);
	}

	this.linkButton.style.backgroundColor = app.colors.green;
	this.homeButton.style.backgroundColor = app.colors.red;

	this.navBar = this.div.getElementsByClassName("navBar")[0];
	if(this.navBar){
		var units = this.navBar.getElementsByClassName('navUnit');
		for(var i=0;i<units.length && i<2;i++){
			var click;
			if(units[i].innerHTML.indexOf('ngredient')>0){
				units[i].style.color = app.colors.ingredients;
				click = function(){ app.navigate('ingredients'); }
			}
			else if(units[i].innerHTML.indexOf('ob')>0){
				units[i].style.color = app.colors.job;
				click = function(){ app.navigate('job'); }
			}
			else if(units[i].innerHTML.indexOf('io')>0){
				units[i].style.color = app.colors.biobot;
				click = function(){ app.navigate('biobot'); }
			}
			else if(units[i].innerHTML.indexOf('og')>0){
				units[i].style.color = app.colors.jog;
				click = function(){ app.navigate('jog'); }
			}

			if(app.mouse) units[i].addEventListener('mousedown',click,false);
			else units[i].addEventListener('touchstart',click,false);
		}
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

BioBot.prototype.resize = function(){

	var buttonWidth = app.pages.jog.mixer.buttonWidth;
	var buttonHeight = app.pages.jog.mixer.buttonHeight;
	var buttonTop = app.top+app.navOffset;

	var fontSize = buttonHeight*.5;
	var lineHeight = fontSize*1.9;

	var jogLeftPos = Math.floor(this.viewScale*app.theWidth);

	this.div.style.top = '0px';
	this.div.style.left = jogLeftPos+'px';

	this.searchButton.style.width = buttonWidth+'px';
	this.searchButton.style.height = buttonHeight+'px';
	this.searchButton.style.left = app.gutter+'px';
	this.searchButton.style.top = buttonTop+'px';
	this.searchButton.style.lineHeight = lineHeight+'px';
	this.searchButton.style.fontSize = fontSize+'px';

	this.linkButton.style.width = buttonWidth+'px';
	this.linkButton.style.height = buttonHeight+'px';
	this.linkButton.style.left = app.gutter+(buttonWidth+app.gutter)+'px';
	this.linkButton.style.top = buttonTop+'px';
	this.linkButton.style.lineHeight = lineHeight+'px';
	this.linkButton.style.fontSize = fontSize+'px';

	this.homeButton.style.width = buttonWidth+'px';
	this.homeButton.style.height = buttonHeight+'px';
	this.homeButton.style.left = app.gutter+(buttonWidth*2+app.gutter*2)+'px';
	this.homeButton.style.top = buttonTop+'px';
	this.homeButton.style.lineHeight = lineHeight+'px';
	this.homeButton.style.fontSize = fontSize+'px';

	this.deviceList.style.left = app.gutter+'px';
	this.deviceList.style.top = buttonTop+buttonHeight+app.gutter+'px';
	this.deviceList.style.width = app.width+'px';
	this.deviceList.style.lineHeight = lineHeight+'px';
	this.deviceList.style.fontSize = fontSize+'px';
	this.deviceList.style.paddingLeft = fontSize/2+'px';

	if(this.navBar){
		this.navBar.style.left = app.gutter-app.borderThickness+'px';
		this.navBar.style.top = app.gutter*.4+'px';
		this.navBar.style.lineHeight = app.gutter*1.3+'px';
		this.navBar.style.fontSize = app.gutter*1.3+'px';

		this.navBar.style.width = app.width+app.borderThickness+'px';
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////