import { Config } from '@stencil/core';


// https://stenciljs.com/docs/config

export const config: Config = {
  outputTargets: [{ type: 'www' }],
  globalScript: 'src/global/app.ts',
  globalStyle: 'src/global/app.css',
  nodeResolve: {
    browser: true,
    preferBuiltins: true // Workaround for https://github.com/ionic-team/stencil/issues/1326
  },
};
