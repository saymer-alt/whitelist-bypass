package android

import (
	"log"
	"strings"

	"whitelist-bypass/relay/common"
	joiner "whitelist-bypass/relay/pion/headless-joiner-common"
	"whitelist-bypass/relay/tunnel"
)

type BlinkHeadlessJoiner struct {
	inner       *joiner.BlinkHeadlessJoiner
	OnConnected func(tunnel.DataTunnel)
}

func NewBlinkHeadlessJoiner(logFn func(string, ...any)) *BlinkHeadlessJoiner {
	if logFn == nil {
		logFn = log.Printf
	}
	inner := joiner.NewBlinkHeadlessJoiner(logFn, RequestResolve, StatusEmitter{}, PCConfigurer{})
	wrapper := &BlinkHeadlessJoiner{inner: inner}
	inner.OnConnected = func(tun tunnel.DataTunnel) {
		if wrapper.OnConnected != nil {
			wrapper.OnConnected(tun)
		}
	}
	return wrapper
}

func (j *BlinkHeadlessJoiner) MarkConfigAcked() { j.inner.MarkConfigAcked() }

func (j *BlinkHeadlessJoiner) Run() {
	j.inner.Status.EmitStatus(common.StatusReady)
	for {
		line, err := ReadStdinLine()
		if err != nil {
			log.Printf("blink-joiner: stdin closed: %v", err)
			return
		}
		if strings.HasPrefix(line, "JOIN:") {
			j.inner.RunWithParams(strings.TrimPrefix(line, "JOIN:"))
			return
		}
	}
}
