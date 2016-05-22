var binary = require('./utils/binary');

function sample(a, b) {
    return {
        input: binary.fromNumber(a, 4).concat(binary.fromNumber(b, 4)),
        output: binary.fromNumber(a + b, 8)
    };
}

function rand(min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
}

var sampleCount = 1000;
var samples = [];

while (--sampleCount) {
    samples.push(sample(rand(0, 16), rand(0, 16)));
}

export default samples;
