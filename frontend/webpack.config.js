const path = require('path');

const mode = 'development';

module.exports = [
  {
    mode: mode,
    entry: {
      'base': './src/base.ts',
      'login': './src/login.ts',
      'consent': './src/consent.ts',
    },
    output: {
      filename: 'bundle_[name].js',
      path: path.resolve(__dirname, 'dist'),
    },
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          use: 'ts-loader',
          exclude: /node_modules/,
        },
      ],
    },
    resolve: {
      extensions: ['.tsx', '.ts', '.js'],
    }
  }
];