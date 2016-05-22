export function fromNumber(num, rank) {
    var binary = num.toString(2).split('');

    return Array(rank - binary.length)
        .fill(0)
        .concat(binary.map( (n) => {
            return parseInt(n, 10);
        }));
}

export function toNumber(binary) {
    return parseInt(binary.join(''), 10);
}

export function parse(arr) {
    return parseInt(arr.map( (v) => {
        return Math.round(v);
    }).map( (v) => {
        return v > 0 ? 1 : 0;
    }).join(''), 2);
}
