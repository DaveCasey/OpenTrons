////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

function Backend(parent){

	this.testGUI = false;

	this.parent = parent;

	this.connected = false;

	this.listening = 1;

	this.power = 1;

	this.pos = {
		'x':0,
		'y':0,
		'z':0,
		'a':0,
		'b':0
	};

	this.max = {
		'x':350,
		'y':200,
		'z':170,
		'a':22
	}

	this.savedObject = undefined;

	this.step = {};

	this.target = {};

	for(var n in this.pos){
		this.target[n] = this.pos[n];
		this.step[n] = 0;
	}

	if(this.testGUI){
		var self = this;
		setInterval(function(){self.update()},50);
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.run = function(job,callback){
	if(job){
		if(this.testGUI){
			if(job.protocol.length>0 && job.ingredients.length>0){
				console.log(job);
				callback();
			}
			else{
				app.throwError('Empty Job');
			}
		}
		else if(job.protocol.length>0 && job.ingredients.length>0){
			opentrons.run(job,function(){
				console.log('success sending job');
				callback();
			},function(){
				console.log('failure sending job');
			});
		}
		else{
			app.throwError('Empty Job');
		}
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.update = function(){

	var moving = false;

	for(var n in this.target){
		if(this.target[n]!=this.pos[n]){
			var diff = this.target[n]-this.pos[n];
			if(Math.abs(diff)>0.01){
				this.step[n] = Math.min(diff/5,0.03);
				this.pos[n] += this.step[n];
				if(this.pos[n]<0 || this.pos[n]>1){
					this.step[n] = 0;
					this.pos[n] = this.target[n];
				}
				else moving = true;
			}
			else{
				this.step[n] = 0;
				this.pos[n] = this.target[n];
			}
		}
	}

	if(moving){
		this.listening = 0;
	}
	else{
		this.listening = 1;
	}

	this.updateParent();
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.buttonStep = function(axis,amount){
	if(this.pos[axis]!=undefined){
		var newPos = this.pos[axis]+(amount*1);
		if(newPos>=0 && newPos<this.max[axis]){
			var msg = {};
			msg[axis] = newPos;
			this.jog(msg);
		}
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.setTarget = function(pos){
	if(this.listening===1){
		for(var n in pos){
			if(this.target[n]!=undefined){
				this.target[n] = Math.min(Math.max(pos[n],0),1);
			}
		}
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.stop = function(){
	if(this.testGUI){
		for(var n in this.target){
			this.target[n] = this.pos[n];
			this.step[n] = 0;
		}
	}
	else{
		opentrons.stop(function(){
			console.log('success stopping');
		},function(){
			console.log('failure stopping');
		});
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.listDevices = function(callback,error){
	var self = this;

	if(this.testGUI){
		var fakeArray = [];
		var amount = Math.floor(Math.random()*10)+1;
		for(var i=0;i<amount;i++){
			fakeArray.push({
				'address':'20:20:20:20',
				'name':'BioBot'+(i+1)
			});
		}
		callback(fakeArray);
	}
	else{
		function onDeviceList(devices){
			callback(devices);
		}

		function onFail(){
			error();
		}

		opentrons.list(onDeviceList,onFail);
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.pause = function(callback){
	if(this.testGUI){
		callback();
	}
	else {
		function failure(){
			//
		};
		opentrons.pause(callback,failure);
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.kill = function(callback){
	if(this.testGUI){
		callback();
	}
	else {
		function success(){
			callback();
		};
		function failure(){
			//
		};
		opentrons.kill(success,failure);
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.resume = function(callback){
	if(this.testGUI){
		callback();
	}
	else {
		function failure(){
			//
		};
		opentrons.resume(callback,failure);
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.listfiles = function(callback){
	if(this.testGUI){
		var string = 'thing_1,thing_2,thing_3';
		var fakeArray = string.split(',');
		callback(fakeArray);
	}
	else {
		function success(string){
			var names = string.split(',');
			callback(names);
		};
		function failure(){
			console.log('Failure listing files');
		};
		opentrons.listfiles(success,failure);
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.save = function(name,object){

	var tempFile = {
		'protocol' : object.protocol,
		'ingredients' : {},
		'pipette' : object.pipette
	};

	for(var i in app.ingredients){
		tempFile.ingredients[i] = {};
		for(var n in app.ingredients[i]){
			if(n==='div'){
				//
			}
			else tempFile.ingredients[i][n] = app.ingredients[i][n];
		}
	}

	if(this.testGUI){
		this.savedObject = tempFile;
	}
	else {

		function success(){
			console.log('yaya, saved!');
		};
		function failure(){
			console.log('failure saving file');
		};

		var fileToSave = JSON.stringify(tempFile);

		opentrons.save(name,fileToSave,success,failure);
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.load = function(name,callback){
	console.log('loading '+name);
	if(this.testGUI){
		callback(this.savedObject);
	}
	else {
		function success(obj){
			callback(obj);
		};
		function failure(){
			console.log('failure loading file');
		};
		opentrons.load(name,success,failure);
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.ping = function(){
	// if(!this.testGUI && this.listening===0){
	// 	var msg = {
	// 		'x' : this.pos.x
	// 	};
	// 	opentrons.jog(JSON.stringify(msg),function(){},function(){
	// 		console.log('error pinging');
	// 	});
	// }
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.home = function(pwr){
	if(this.testGUI){
		console.log('yaya, homed');
	}
	else{
		opentrons.home(function(){
			console.log('success homing');
		},function(){
			console.log('error homing');
		});
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.setPower = function(pwr){
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.connect = function(address,callback){
	var self = this;

	if(self.testGUI){
		callback();
	}
	else{
		function onConnection(){
			self.updateParent();

			callback();
			
			opentrons.subscribe('\n',function(data){
				if(data){
					console.log('RECEIVED: '+data);
					data = JSON.parse(data);
					for(var n in data){
						if(n==='listening' || n==='power') self[n] = data[n];
						else{
							self.pos[n] = data[n]/self.max[n];
						}
					}
					self.updateParent();
				}
			},function(){});
		}

		function onFailConnect(){
			console.log(' failed connecting');
		}

		opentrons.connect(address,onConnection,onFailConnect);
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.disconnect = function(callback){
	if(this.testGUI){
		callback();
	}
	else{
		opentrons.disconnect(callback,function(){
			console.log('ERROR trying to disconnect from BioBot');
		});
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.jog = function(json){
	// for(var n in json){
	// 	json[n] = Math.max(Math.min(json[n],1),0);
	// }

	console.log('Sending: '+JSON.stringify(json));

	if(this.testGUI){
		if(json.stop!=undefined) this.stop();
		else if(json.reset!=undefined) this.reset();
		else if(json.power!=undefined) this.setPower(json.power);
		else this.setTarget(json);
	}
	else{
		for(var n in json){
			json[n] = json[n] * this.max[n];
			console.log('Sending: '+json[n]);
		}
		//console.log('sending '+JSON.stringify(json));
		function onSuccess(){
			//console.log('SUCCESS SENDING DATA');
		}
		function onError(){
			//console.log('FAILURE SENDING DATA :(');
		}
		opentrons.jog(JSON.stringify(json),onSuccess,onError);
	}
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Backend.prototype.updateParent = function(data){

	var update = {
		'x' : this.pos.x,
		'y' : this.pos.y,
		'z' : this.pos.z,
		'a' : this.pos.a,
		'b' : this.pos.b,
		'listening' : this.listening,
		'power' : this.power
	}

	this.parent.onRobotUpdate(update);
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////
