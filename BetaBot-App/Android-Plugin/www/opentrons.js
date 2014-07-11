/*global cordova*/
module.exports = {

    connect: function (macAddress, success, failure) {
        cordova.exec(success, failure, "OTBTAlpha", "connect", [macAddress]);
    },

    // Android only - see http://goo.gl/1mFjZY
    //connectInsecure: function (macAddress, success, failure) {
    //    cordova.exec(success, failure, "OTBTAlpha", "connectInsecure", [macAddress]);
    //},

    disconnect: function (success, failure) {
        cordova.exec(success, failure, "OTBTAlpha", "disconnect", []);
    },

    // list bound devices
    list: function (success, failure) {
        cordova.exec(success, failure, "OTBTAlpha", "list", []);
    },

    isEnabled: function (success, failure) {
        cordova.exec(success, failure, "OTBTAlpha", "isEnabled", []);
    },

    isConnected: function (success, failure) {
        cordova.exec(success, failure, "OTBTAlpha", "isConnected", []);
    },

    // the number of bytes of data available to read is passed to the success function
    //available: function (success, failure) {
    //    cordova.exec(success, failure, "OTBTAlpha", "available", []);
    //},

    // read all the data in the buffer
    //read: function (success, failure) {
    //    cordova.exec(success, failure, "OTBTAlpha", "read", []);
    //},

    // reads the data in the buffer up to and including the delimiter
    //readUntil: function (delimiter, success, failure) {
    //    cordova.exec(success, failure, "OTBTAlpha", "readUntil", [delimiter]);
    //},

    // writes data to the bluetooth serial port - data must be a string
    //write: function (data, success, failure) {
    //    cordova.exec(success, failure, "OTBTAlpha", "write", [data]);
    //},

    // calls the success callback when new data is available
    subscribe: function (delimiter, success, failure) {
        cordova.exec(success, failure, "OTBTAlpha", "subscribe", [delimiter]);
    },
    
    // removes data subscription
    unsubscribe: function (success, failure) {
        cordova.exec(success, failure, "OTBTAlpha", "unsubscribe", []);
    },

    // clears the data buffer
    //clear: function (success, failure) {
    //    cordova.exec(success, failure, "OTBTAlpha", "clear", []);
    //},
    
    // reads the RSSI of the *connected* peripherial
    //readRSSI: function (success, failure) {
    //    cordova.exec(success, failure, "OTBTAlpha", "readRSSI", []);        
    //},
    
    //added for simple jog commands
    jog: function (jogcommand, success, failure) {
	cordova.exec(success, failure, "OTBTAlpha", "jog", [jogcommand]);
    },
	
    setDimensions: function (dimensions, success, failure) {
	cordova.exec(success, failure, "OTBTAlpha", "setdimensions", [dimensions]);
    },
	
    getDimensions: function (success, failure) {
	cordova.exec(success, failure, "OTBTAlpha", "getdimensions", []);
    },
	
    home: function (success, failure) {
	cordova.exec(success, failure, "OTBTAlpha", "home", []);
    },

    stop: function (success, failure) {
	cordova.exec(success, failure, "OTBTAlpha", "stop", []);
    },

    reset: function (success, failure) {
	cordova.exec(success, failure, "OTBTAlpha", "reset", []);
    },
    
    run: function (job, success, failure) {
    	cordova.exec(success, failure, "OTBTAlpha", "run", [job]);
    }
};
