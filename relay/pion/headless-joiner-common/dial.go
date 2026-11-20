package joiner

import (
	"context"
	"net"
	"net/http"
	"time"

	"github.com/kulikov0/headless-client"
)

func resolvingDialContext(resolveFn ResolveFunc) func(ctx context.Context, network, addr string) (net.Conn, error) {
	if resolveFn == nil {
		return nil
	}
	return func(ctx context.Context, network, addr string) (net.Conn, error) {
		host, port, err := net.SplitHostPort(addr)
		if err != nil {
			return nil, err
		}
		resolvedIP, err := resolveFn(host)
		if err != nil {
			return nil, err
		}
		dialer := headless.ChromeDialer()
		dialer.Timeout = 10 * time.Second
		return dialer.DialContext(ctx, network, resolvedIP+":"+port)
	}
}

func resolvingHTTPClient(resolveFn ResolveFunc) *http.Client {
	return &http.Client{
		Timeout:   60 * time.Second,
		Transport: headless.ChromeWindows.Transport(headless.TLSOptions{DialContext: resolvingDialContext(resolveFn)}),
	}
}
