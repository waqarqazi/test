module.exports = {
  presets: ['module:metro-react-native-babel-preset'],
  plugins: [
    [
      'module-resolver',
      {
        alias: {
          screens: './src/screens',
          components: './src/components',
          assets: './src/assets',
          navigation: './src/router',
          services: './src/services',
          store: './src/redux',
          // types: './src/redux/types',
          utils: './src/utils',
          redux: './src/redux',
        },
      },
    ],
    'react-native-reanimated/plugin',
  ],
};
