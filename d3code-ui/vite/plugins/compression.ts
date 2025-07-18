import compression from 'vite-plugin-compression';

export default (env: any) => {
    const {VITE_BUILD_COMPRESS} = env;
    const plugin: any[] = [];
    if (VITE_BUILD_COMPRESS) {
        const compressList = VITE_BUILD_COMPRESS.split(',');
        if (compressList.includes('gzip')) {
            // http://doc.d3code.vip/d3code-vue/other/faq.html#使用gzip解压缩静态文件
            plugin.push(
                compression({
                    ext: '.gz',
                    deleteOriginFile: false
                })
            );
        }
        if (compressList.includes('brotli')) {
            plugin.push(
                compression({
                    ext: '.br',
                    algorithm: 'brotliCompress',
                    deleteOriginFile: false
                })
            );
        }
    }
    return plugin;
};
