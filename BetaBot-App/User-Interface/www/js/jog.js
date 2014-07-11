////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

function Jog(parent){
	this.parent = parent;

	this.div = document.getElementById('jog_container');

	this.bed = undefined;
	this.mixer = undefined;

	this.showButtons = false;

	this.viewScale = 1;
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Jog.prototype.setup = function(){
	this.bed = new Bed(this);
	this.bed.setup();

	this.mixer = new Mixer(this);
	this.mixer.setup();
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Jog.prototype.update = function(){
	if(app.backend.pos.z<0.1){
		if(this.showButtons){
			this.showButtons = false;
			document.getElementById('bed_buttons').style.display = 'none';
		}
	}
	else{
		if(!this.showButtons){
			this.showButtons = true;
			document.getElementById('bed_buttons').style.display = 'block';
		}
	}

	this.bed.draw();
	this.mixer.update();
}

Jog.prototype.setIngredient = function(ing){
	this.mixer.setIngredient(ing);
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////

Jog.prototype.resize = function(){

	var jogLeftPos = Math.floor(this.viewScale*app.theWidth);

	this.div.style.top = '0px';
	this.div.style.left = jogLeftPos+'px';

	// set the BED's location on the page
	this.mixer.left = (app.gutter-app.borderThickness);
	this.mixer.width = app.width+(app.borderThickness);

	this.mixer.gridSize = this.mixer.width/6;
	this.mixer.buttonWidth = (this.mixer.width-app.gutter*2)/3;
	this.mixer.buttonHeight = this.mixer.gridSize+(app.borderThickness*2);

	this.mixer.height = (this.mixer.buttonHeight*4)+(app.gutter*3);
	this.mixer.top = app.theHeight-(this.mixer.height+app.gutter-app.borderThickness);

	this.mixer.resize();

	// set the BED's location on the page
	this.bed.left = (app.gutter-app.borderThickness);
	this.bed.top = app.top+app.navOffset;
	this.bed.width = app.width-app.borderThickness;
	this.bed.height = (this.mixer.top-this.bed.top)-(app.gutter+app.borderThickness);

	this.bed.resize();
}

////////////////////////////////////
////////////////////////////////////
////////////////////////////////////