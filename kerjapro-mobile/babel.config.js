module.exports = function (api) {
  api.cache(true);
  return {
    presets: ['babel-preset-expo'],
    // react-native-reanimated/plugin removed: Reanimated 4 (SDK 54)
    // requires react-native-worklets peer dep which is not yet installed.
    // Add back when gesture/animation screens are built.
  };
};
