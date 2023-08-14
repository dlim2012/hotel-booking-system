const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
    // app.use(
    //     '/api/v1/**',
    //     createProxyMiddleware({
    //         target: 'http://73.253.48.199:9000',
    //         changeOrigin: true,
    //     })
    // );
    // app.use(
    //     '/api/v1/**',
    //     createProxyMiddleware({
    //         target: 'http://73.253.48.199:8001',
    //         changeOrigin: true,
    //     })
    // );
    // app.use(
    //     '/api/v1/**',
    //     createProxyMiddleware({
    //         target: 'http://10.0.0.110:8001',
    //         changeOrigin: true,
    //     })
    // );
    app.use(
        '/api/v1/**',
        createProxyMiddleware({
            target: 'http://10.0.0.110:9000',
            changeOrigin: true,
        })
    );
    // app.use(
    //     '/api/v1/user',
    //     createProxyMiddleware({
    //         // target: 'http://10.0.0.110:9000',
    //         target: 'http://10.0.0.24:9002',
    //         changeOrigin: true,
    //     })
    // );
    // app.use(
    //     '/api/v1/booking',
    //     createProxyMiddleware({
    //         target: 'http://10.0.0.24:9003',
    //         changeOrigin: true,
    //     })
    // );
    // app.use(
    //     '/api/v1/hotel',
    //     createProxyMiddleware({
    //         target: 'http://10.0.0.24:9001',
    //         changeOrigin: true,
    //     })
    // );
    // app.use(
    //     '/api/v1/search',
    //     createProxyMiddleware({
    //         target: 'http://10.0.0.24:9007',
    //         changeOrigin: true,
    //     })
    // );
    // app.use(
    //     '/api/v1/booking-management',
    //     createProxyMiddleware({
    //         target: 'http://10.0.0.24:9005',
    //         changeOrigin: true,
    //     })
    // );
};