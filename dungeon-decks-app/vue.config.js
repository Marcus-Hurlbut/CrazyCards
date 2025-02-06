const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // chainWebpack: (config) => {
  //   config.module
  //     .rule('svg')
  //     .use('vue-loader')
  //     .tap((options) => {
  //       options.compilerOptions.isCustomElement = (tag) => tag.startsWith('fe');
  //       return options;
  //     });
  // }
})
