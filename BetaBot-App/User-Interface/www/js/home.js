////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

function Home(parent){
	this.parent = parent;

	this.div = document.getElementById('home_container');

	this.searchButton = document.getElementById('searchButton');
	this.linkButton = document.getElementById('linkButton');

	this.runButton = document.getElementById('runButton');
	this.killButton = document.getElementById('killButton');

	this.pauseButton = document.getElementById('pauseButton');
	this.resumeButton = document.getElementById('resumeButton');

	this.deviceList = document.getElementById('deviceList');
	this.deviceList.style.color = app.colors.grey;

	this.viewScale = 0;
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Home.prototype.update = function(){
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Home.prototype.listDevices = function(){
	while(this.deviceList.firstChild){
		this.deviceList.removeChild(this.deviceList.firstChild);
	}
	function onSuccess(devices){
		if(devices.length>0){
			this.linkButton.style.backgroundColor = app.colors.red;
		}
		else{
			this.linkButton.style.backgroundColor = app.colors.lightGrey;
		}
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

Home.prototype.onConnect = function(){
	this.linkButton.innerHTML = 'Unlink';
	this.linkButton.style.backgroundColor = app.colors.lightGrey;
	this.linkButton.style.color = app.colors.green;

	this.searchButton.style.backgroundColor = app.colors.lightGrey;

	app.backend.connected = true;

	this.runButton.style.backgroundColor = app.colors.green;
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Home.prototype.onDisconnect = function(){
	this.linkButton.innerHTML = 'Link';
	this.linkButton.style.backgroundColor = app.colors.red;
	this.linkButton.style.color = 'white';

	app.backend.connected = false;

	this.searchButton.style.backgroundColor = app.colors.blue;

	self.runButton.style.backgroundColor = app.colors.lightGrey;
	self.killButton.style.backgroundColor = app.colors.lightGrey;

	self.pauseButton.style.backgroundColor = app.colors.lightGrey;
	self.resumeButton.style.backgroundColor = app.colors.lightGrey;

	this.runButton.style.backgroundColor = app.colors.lightGrey;
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Home.prototype.setup = function(){
	this.div.style.backgroundColor = app.colors.grey;

	this.searchButton.style.backgroundColor = app.colors.blue;
	this.linkButton.style.backgroundColor = app.colors.lightGrey;
	this.linkButton.style.color = 'white';

	this.runButton.style.backgroundColor = app.colors.lightGrey;
	this.killButton.style.backgroundColor = app.colors.lightGrey;

	this.pauseButton.style.backgroundColor = app.colors.lightGrey;
	this.resumeButton.style.backgroundColor = app.colors.lightGrey;

	var self = this;

	function initList(){
		self.listDevices();
	}

	function connectButton(){
		if(self.linkButton.innerHTML.trim()==='Link'){
			var address = self.deviceList.value;
			if(address){
				app.backend.connect(address,self.onConnect);
			}
		}
		else{
			self.linkButton.innerHTML='Link';
			app.backend.disconnect(self.onDisconnect);
		}
	}

	function onRun(){
		if(app.backend.connected){
			app.backend.run( app.prepareJob() , function(){
				self.runButton.style.backgroundColor = app.colors.lightGrey;
				self.killButton.style.backgroundColor = app.colors.red;
				self.pauseButton.style.backgroundColor = app.colors.blue;
				self.resumeButton.style.backgroundColor = app.colors.lightGrey;
			});
		}
	}
	function onKill(){
		if(app.backend.connected){
			app.backend.kill(function(){
				self.runButton.style.backgroundColor = app.colors.green;
				self.killButton.style.backgroundColor = app.colors.lightGrey;
				self.pauseButton.style.backgroundColor = app.colors.lightGrey;
				self.resumeButton.style.backgroundColor = app.colors.lightGrey;
			});
		}
	}

	function onPause(){
		if(app.backend.connected){
			app.backend.pause(function(){
				self.pauseButton.style.backgroundColor = app.colors.lightGrey;
				self.resumeButton.style.backgroundColor = app.colors.blue;
			});
		}
	}
	function onResume(){
		if(app.backend.connected){
			app.backend.resume(function(){
				self.pauseButton.style.backgroundColor = app.colors.blue;
				self.resumeButton.style.backgroundColor = app.colors.lightGrey;
			});
		}
	}

	this.searchButton.addEventListener('click',initList,false);
	this.linkButton.addEventListener('click',connectButton,false);

	this.runButton.addEventListener('click',onRun,false);
	this.killButton.addEventListener('click',onKill,false);

	this.pauseButton.addEventListener('click',onPause,false);
	this.resumeButton.addEventListener('click',onResume,false);
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Home.prototype.resize = function(){

	var buttonWidth = (app.width-app.gutter)/2;
	var buttonHeight = app.pages.jog.mixer.buttonHeight;
	var buttonTop = app.height+(buttonHeight*-4)+(app.gutter*-2);

	var fontSize = buttonHeight*.5;
	var lineHeight = fontSize*1.9;

	var jogLeftPos = Math.floor(this.viewScale*app.theWidth);

	var title = document.getElementById('openTronsDiv');
	title.style.fontSize = fontSize*2+'px';
	title.style.lineHeight = lineHeight*1.4+'px';
	title.style.marginTop = app.navOffset*.8+'px';
	var subTitle = title.children[0];
	subTitle.style.color = app.colors.blue;
	subTitle.style.fontSize = fontSize+'px';
	subTitle.style.lineHeight = lineHeight+'px';
	subTitle.style.marginTop = -fontSize+'px';

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

	this.deviceList.style.left = app.gutter+'px';
	this.deviceList.style.top = buttonTop+buttonHeight+app.gutter+'px';
	this.deviceList.style.width = app.width+'px';
	this.deviceList.style.lineHeight = lineHeight+'px';
	this.deviceList.style.fontSize = fontSize+'px';
	this.deviceList.style.paddingLeft = fontSize/2+'px';

	this.runButton.style.width = (app.width-app.gutter)/2+'px';
	this.runButton.style.height = buttonHeight+'px';
	this.runButton.style.left = app.gutter+'px';
	this.runButton.style.top = buttonTop+buttonHeight*2+app.gutter*2+'px';
	this.runButton.style.lineHeight = lineHeight+'px';
	this.runButton.style.fontSize = fontSize+'px';

	this.killButton.style.width = (app.width-app.gutter)/2+'px';
	this.killButton.style.height = buttonHeight+'px';
	this.killButton.style.left = app.gutter+(buttonWidth+app.gutter)+'px';
	this.killButton.style.top = buttonTop+buttonHeight*2+app.gutter*2+'px';
	this.killButton.style.lineHeight = lineHeight+'px';
	this.killButton.style.fontSize = fontSize+'px';

	this.pauseButton.style.width = (app.width-app.gutter)/2+'px';
	this.pauseButton.style.height = buttonHeight+'px';
	this.pauseButton.style.left = app.gutter+'px';
	this.pauseButton.style.top = buttonTop+buttonHeight*3+app.gutter*3+'px';
	this.pauseButton.style.lineHeight = lineHeight+'px';
	this.pauseButton.style.fontSize = fontSize+'px';

	this.resumeButton.style.width = (app.width-app.gutter)/2+'px';
	this.resumeButton.style.height = buttonHeight+'px';
	this.resumeButton.style.left = app.gutter+(buttonWidth+app.gutter)+'px';
	this.resumeButton.style.top = buttonTop+buttonHeight*3+app.gutter*3+'px';
	this.resumeButton.style.lineHeight = lineHeight+'px';
	this.resumeButton.style.fontSize = fontSize+'px';
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////