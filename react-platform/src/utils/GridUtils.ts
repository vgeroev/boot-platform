function getMatrix<T>(
    array: Array<T>,
    cRow: number,
    cCol: number,
): Array<Array<T>> {
    const matrix: Array<Array<T>> = [];
    for (let i = 0; i < cRow; i++) {
        matrix.push(array.slice(i * cCol, (i + 1) * cCol));
    }

    const cMatrix = cRow * cCol;
    if (cMatrix < array.length) {
        matrix.push(array.slice(cMatrix));
    }

    return matrix;
}

export { getMatrix };
